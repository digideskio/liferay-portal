/**
 * Copyright (c) 2000-2007 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.plugin;

import com.liferay.portal.kernel.plugin.Plugin;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.ReleaseInfo;
import com.liferay.portal.util.SAXReaderFactory;
import com.liferay.util.GetterUtil;
import com.liferay.util.Html;
import com.liferay.util.License;
import com.liferay.util.Validator;
import com.liferay.util.Version;
import com.liferay.util.XSSUtil;
import com.liferay.util.xml.XMLSafeReader;

import java.io.IOException;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <a href="PluginUtil.java.html"><b><i>View Source</i></b></a>
 *
 * @author Jorge Ferrer
 *
 */
public class PluginUtil {

	public static Collection getAvailableTags() {
		return _availableTagsCache;
	}

	public static Date getLastUpdateDate() {
		return _lastUpdateDate;
	}

	public static Plugin getPluginById(String moduleId, String repositoryURL)
		throws DocumentException, IOException, PluginException {

		return getRepository(repositoryURL).findPluginByModuleId(moduleId);
	}

	public static Plugin getPluginByURL(String url) throws PluginException {
		String[] repositoryURLs = getRepositoryURLs();

		for (int i = 0; i < repositoryURLs.length; i++) {
			String repositoryURL = repositoryURLs[i];

			try {
				PluginRepository repository =  getRepository(repositoryURL);

				return repository.findPluginByArtifactURL(url);
			}
			catch (PluginException pe) {
				_log.info("Error loading repository " + repositoryURL, pe);
			}
		}

		return null;
	}

	public static String[] getRepositoryURLs() throws PluginException {
		try {
			return PrefsPropsUtil.getStringArray(PropsUtil.PLUGIN_REPOSITORIES);
		}
		catch (Exception e) {
			throw new PluginException("Could not read repository list", e);
		}
	}

	public static PluginRepository getRepository(String repositoryURL)
		throws PluginException {

		PluginRepository repository =
			(PluginRepository)_repositoryCache.get(repositoryURL);

		if (repository != null) {
			return repository;
		}

		return _loadRepository(repositoryURL);
	}

	public static String[] getSupportedTypes() {
		return PropsUtil.getArray(PropsUtil.PLUGIN_TYPES);
	}

	public static List search(String type, String tags, String repositoryURL)
		throws PluginException {

		SortedMap plugins = new TreeMap();

		String[] repositoryURLs = null;

		if (Validator.isNull(repositoryURL)) {
			repositoryURLs = getRepositoryURLs();
		}
		else {
			repositoryURLs = new String[] {repositoryURL};
		}

		for (int i = 0; i < repositoryURLs.length; i++) {
			try {
				PluginRepository repository = getRepository(repositoryURLs[i]);

				Collection repoPlugins = repository.search(type, tags);

				Iterator itr = repoPlugins.iterator();

				while (itr.hasNext()) {
					Plugin plugin = (Plugin)itr.next();

					// If the plugin was in another repository keep the
					// latest version

					Plugin previous = (Plugin)plugins.get(plugin);

					if ((previous != null) &&
						plugin.isLaterVersionThan(previous)) {

						plugins.remove(previous);
					}

					plugins.put(plugin, plugin);
				}
			}
			catch(PluginException pe) {
				_log.info("Error loading repository " + repositoryURL, pe);
			}
		}

		return new ArrayList(plugins.keySet());
	}

	public static RepositoryReport reloadRepositories() throws PluginException {
		RepositoryReport report = new RepositoryReport();

		String[] repositoryURLs = getRepositoryURLs();

		for (int i = 0; i < repositoryURLs.length; i++) {
			String repositoryURL = repositoryURLs[i];

			try {
				_loadRepository(repositoryURL);

				report.addSuccess(repositoryURL);
			}
			catch(PluginException pe) {
				report.addError(repositoryURL, pe);

				_log.info(
					"Error loading repository " + repositoryURL + " " +
						pe.toString());
			}
		}
		return report;
	}

	private static PluginRepository _loadRepository(String repositoryURL)
		throws PluginException {

		PluginRepository repository = null;

		String pluginsXmlURL =
			repositoryURL + StringPool.SLASH + _PLUGINS_XML_FILENAME;

		try {
			HttpClient client = new HttpClient();

			int timeout = GetterUtil.getInteger(
				PropsUtil.get(PropsUtil.PLUGIN_TIMEOUT_LIST));

			client.getHttpConnectionManager().getParams().setConnectionTimeout(
				timeout);

			GetMethod getFile = new GetMethod(pluginsXmlURL);

			int responseCode = client.executeMethod(getFile);

			if (responseCode != 200) {
				throw new PluginException(
					"Cannot download file " + pluginsXmlURL +
						" because of response code " + responseCode);
			}

			byte[] bytes = getFile.getResponseBody();

			getFile.releaseConnection();

			if ((bytes != null) && (bytes.length > 0)) {
				repository = _parsePluginsXml(new String(bytes), repositoryURL);

				_repositoryCache.put(repositoryURL, repository);
				_availableTagsCache.addAll(repository.getTags());
				_lastUpdateDate = new Date();

				return repository;
			}
			else {
				_lastUpdateDate = new Date();

				throw new PluginException("Download returned 0 bytes");
			}
		}
		catch (MalformedURLException mue) {
			_repositoryCache.remove(repositoryURL);

			throw new PluginException("Invalid URL " + pluginsXmlURL, mue);
		}
		catch (IOException ioe) {
			_repositoryCache.remove(repositoryURL);

			throw new PluginException(
				"Error communicating with repository " + repositoryURL, ioe);
		}
		catch (DocumentException de) {
			_repositoryCache.remove(repositoryURL);

			throw new PluginException(
				"Error parsing plugin list for repository " + repositoryURL,
				de);
		}
	}

	private static PluginRepository _parsePluginsXml(
			String xml, String repositoryURL)
		throws DocumentException, IOException {

		List supportedPluginTypes = Arrays.asList(getSupportedTypes());

		if (_log.isDebugEnabled()) {
			_log.debug("Plugins " + xml);
		}

		PluginRepository plugins = new PluginRepository();

		if (xml == null) {
			return plugins;
		}

		SAXReader reader = SAXReaderFactory.getInstance();

		Document doc = reader.read(new XMLSafeReader(xml));

		Element root = doc.getRootElement();

		Iterator itr = root.elements().iterator();

		while (itr.hasNext()) {
			Element pluginEl = (Element)itr.next();

			String name = pluginEl.elementText("name");

			if (_log.isDebugEnabled()) {
				_log.debug("Reading plugin definition " + name);
			}

			Plugin plugin = new PluginImpl(
				GetterUtil.getString(pluginEl.elementText("module-id")));

			List liferayVersions = _readList(
				pluginEl.element("liferay-versions"), "liferay-version");

			String type = GetterUtil.getString(pluginEl.elementText("type"));

			if (!_isCurrentVersionSupported(liferayVersions) ||
				!supportedPluginTypes.contains(type)) {

				continue;
			}

			plugin.setName(_readText(name));
			plugin.setAuthor(_readText(pluginEl.elementText("author")));
			plugin.setType(type);
			plugin.setLicenses(
				_readLicenseList(pluginEl.element("licenses"), "license"));
			plugin.setLiferayVersions(liferayVersions);
			plugin.setTags(_readList(pluginEl.element("tags"), "tag"));
			plugin.setShortDescription(
				_readText(pluginEl.elementText("short-description")));
			plugin.setLongDescription(
				_readHtml(pluginEl.elementText("long-description")));
			plugin.setScreenshotURLs(
				_readList(pluginEl.element("screenshots"), "screenshot-url"));
			plugin.setPageURL(_readText(pluginEl.elementText("page-url")));
			plugin.setRepositoryURL(repositoryURL);
			plugin.setRecommendedWARName(
				_readText(pluginEl.elementText("recommended-war-name")));

			plugins.addPlugin(plugin);
		}

		return plugins;
	}

	private static boolean _isCurrentVersionSupported(List versions) {
		for (int i = 0; i < versions.size(); i++) {
			Version supportedVersion = new Version((String)versions.get(i));

			Version currentVersion = new Version(ReleaseInfo.getVersion());

			if (supportedVersion.includes(currentVersion)) {
				return true;
			}
		}

		return false;
	}

	private static String _readHtml(String text) {
		return XSSUtil.strip(GetterUtil.getString(text));
	}

	private static List _readLicenseList(Element parent, String childTagName) {
		List result = new ArrayList();

		Iterator itr = parent.elements(childTagName).iterator();

		while (itr.hasNext()) {
			Element tagEl = (Element)itr.next();

			License license = new License();

			license.setName(tagEl.getText());

			Attribute osiApproved = tagEl.attribute("osi-approved");

			if (osiApproved != null) {
				license.setOsiApproved(
					GetterUtil.getBoolean(osiApproved.getText()));
			}

			Attribute url = tagEl.attribute("url");

			if (url != null) {
				license.setUrl(url.getText());
			}

			result.add(license);
		}

		return result;
	}

	private static List _readList(Element parent, String childTagName) {
		List result = new ArrayList();

		if (parent != null) {
			Iterator itr = parent.elements(childTagName).iterator();

			while (itr.hasNext()) {
				Element tagEl = (Element)itr.next();

				result.add(tagEl.getText());
			}
		}

		return result;
	}

	private static String _readText(String text) {
		return Html.stripHtml(GetterUtil.getString(text));
	}

	private static final String _PLUGINS_XML_FILENAME = "liferay-plugins.xml";

	private static Log _log = LogFactory.getLog(PluginUtil.class);

	private static Map _repositoryCache = new HashMap();
	private static Set _availableTagsCache = new TreeSet();
	private static Date _lastUpdateDate = null;

}