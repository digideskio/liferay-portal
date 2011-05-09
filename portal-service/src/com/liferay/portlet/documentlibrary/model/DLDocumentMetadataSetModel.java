/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.documentlibrary.model;

import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.service.ServiceContext;

import com.liferay.portlet.expando.model.ExpandoBridge;

import java.io.Serializable;

/**
 * The base model interface for the DLDocumentMetadataSet service. Represents a row in the &quot;DLDocumentMetadataSet&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation {@link com.liferay.portlet.documentlibrary.model.impl.DLDocumentMetadataSetModelImpl} exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link com.liferay.portlet.documentlibrary.model.impl.DLDocumentMetadataSetImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DLDocumentMetadataSet
 * @see com.liferay.portlet.documentlibrary.model.impl.DLDocumentMetadataSetImpl
 * @see com.liferay.portlet.documentlibrary.model.impl.DLDocumentMetadataSetModelImpl
 * @generated
 */
public interface DLDocumentMetadataSetModel extends BaseModel<DLDocumentMetadataSet> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. All methods that expect a d l document metadata set model instance should use the {@link DLDocumentMetadataSet} interface instead.
	 */

	/**
	 * Gets the primary key of this d l document metadata set.
	 *
	 * @return the primary key of this d l document metadata set
	 */
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this d l document metadata set
	 *
	 * @param pk the primary key of this d l document metadata set
	 */
	public void setPrimaryKey(long pk);

	/**
	 * Gets the uuid of this d l document metadata set.
	 *
	 * @return the uuid of this d l document metadata set
	 */
	@AutoEscape
	public String getUuid();

	/**
	 * Sets the uuid of this d l document metadata set.
	 *
	 * @param uuid the uuid of this d l document metadata set
	 */
	public void setUuid(String uuid);

	/**
	 * Gets the document metadata set ID of this d l document metadata set.
	 *
	 * @return the document metadata set ID of this d l document metadata set
	 */
	public long getDocumentMetadataSetId();

	/**
	 * Sets the document metadata set ID of this d l document metadata set.
	 *
	 * @param documentMetadataSetId the document metadata set ID of this d l document metadata set
	 */
	public void setDocumentMetadataSetId(long documentMetadataSetId);

	/**
	 * Gets the class name of the model instance this d l document metadata set is polymorphically associated with.
	 *
	 * @return the class name of the model instance this d l document metadata set is polymorphically associated with
	 */
	public String getClassName();

	/**
	 * Gets the class name ID of this d l document metadata set.
	 *
	 * @return the class name ID of this d l document metadata set
	 */
	public long getClassNameId();

	/**
	 * Sets the class name ID of this d l document metadata set.
	 *
	 * @param classNameId the class name ID of this d l document metadata set
	 */
	public void setClassNameId(long classNameId);

	/**
	 * Gets the class p k of this d l document metadata set.
	 *
	 * @return the class p k of this d l document metadata set
	 */
	public long getClassPK();

	/**
	 * Sets the class p k of this d l document metadata set.
	 *
	 * @param classPK the class p k of this d l document metadata set
	 */
	public void setClassPK(long classPK);

	/**
	 * Gets the d d m structure ID of this d l document metadata set.
	 *
	 * @return the d d m structure ID of this d l document metadata set
	 */
	public long getDDMStructureId();

	/**
	 * Sets the d d m structure ID of this d l document metadata set.
	 *
	 * @param DDMStructureId the d d m structure ID of this d l document metadata set
	 */
	public void setDDMStructureId(long DDMStructureId);

	/**
	 * Gets the document type ID of this d l document metadata set.
	 *
	 * @return the document type ID of this d l document metadata set
	 */
	public long getDocumentTypeId();

	/**
	 * Sets the document type ID of this d l document metadata set.
	 *
	 * @param documentTypeId the document type ID of this d l document metadata set
	 */
	public void setDocumentTypeId(long documentTypeId);

	/**
	 * Gets the file version ID of this d l document metadata set.
	 *
	 * @return the file version ID of this d l document metadata set
	 */
	public long getFileVersionId();

	/**
	 * Sets the file version ID of this d l document metadata set.
	 *
	 * @param fileVersionId the file version ID of this d l document metadata set
	 */
	public void setFileVersionId(long fileVersionId);

	public boolean isNew();

	public void setNew(boolean n);

	public boolean isCachedModel();

	public void setCachedModel(boolean cachedModel);

	public boolean isEscapedModel();

	public void setEscapedModel(boolean escapedModel);

	public Serializable getPrimaryKeyObj();

	public ExpandoBridge getExpandoBridge();

	public void setExpandoBridgeAttributes(ServiceContext serviceContext);

	public Object clone();

	public int compareTo(DLDocumentMetadataSet dlDocumentMetadataSet);

	public int hashCode();

	public DLDocumentMetadataSet toEscapedModel();

	public String toString();

	public String toXmlString();
}