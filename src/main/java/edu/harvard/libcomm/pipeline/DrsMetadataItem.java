package edu.harvard.libcomm.pipeline;

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;

public class DrsMetadataItem {

    @Field
    private String id;
    @Field("fileDeliveryURL")
    private String url;
    @Field("uriType")
    private String deliveryType;
    @Field
    private String urn;
    @Field
    private String drsFileId;
    @Field
    private String drsObjectId;
    @Field
    private String accessFlag;
    @Field
    private String lastModifiedDate;
    @Field
    private String insertionDate;
    @Field
    private String ownerSuppliedName;
    @Field("contentModelCode")
    private String cmCode;
    @Field("contentModel")
    private String alias;
    @Field
    private String ownerCode;
    @Field
    private String ownerCodeDisplayName;
    @Field
    private String metsLabel;
    @Field("harvardMetadataLink")
    private ArrayList<String> harvardMetadataLinks;
    @Field
    private String viewText;
    @Field
    private String maxImageDeliveryDimension;
    @Field
    private String mimeType;
    @Field
    private String suppliedFilename;
    @Field
    private String thumbnailURL;
    @Field
    private String status;

    @Field
    private String processingDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public String getDrsFileId() {
        return drsFileId;
    }

    public void setDrsFileId(String drsFileId) {
        this.drsFileId = drsFileId;
    }

    public String getDrsObjectId() {
        return drsObjectId;
    }

    public void setDrsObjectId(String drsObjectId) {
        this.drsObjectId = drsObjectId;
    }

    public String getAccessFlag() {
        return accessFlag;
    }

    public void setAccessFlag(String accessFlag) {
        this.accessFlag = accessFlag;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getInsertionDate() {
        return insertionDate;
    }

    public void setInsertionDate(String insertionDate) {
        this.insertionDate = insertionDate;
    }

    public String getOwnerSuppliedName() {
        return ownerSuppliedName;
    }

    public void setOwnerSuppliedName(String ownerSuppliedName) {
        this.ownerSuppliedName = ownerSuppliedName;
    }

    public String getCmCode() {
        return cmCode;
    }

    public void setCmCode(String cmCode) {
        this.cmCode = cmCode;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getOwnerCodeDisplayName() {
        return ownerCodeDisplayName;
    }

    public void setOwnerCodeDisplayName(String ownerCodeDisplayName) {
        this.ownerCodeDisplayName = ownerCodeDisplayName;
    }

    public String getMetsLabel() {
        return metsLabel;
    }

    public void setMetsLabel(String metsLabel) {
        this.metsLabel = metsLabel;
    }

    public ArrayList<String> getHarvardMetadataLinks() {
        return harvardMetadataLinks;
    }

    public void setHarvardMetadataLinks(ArrayList<String> harvardMetadataLinks) {
        this.harvardMetadataLinks = harvardMetadataLinks;
    }

    public String getViewText() {
        return viewText;
    }

    public void setViewText(String viewText) {
        this.viewText = viewText;
    }

    public String getMaxImageDeliveryDimension() {
        return maxImageDeliveryDimension;
    }

    public void setMaxImageDeliveryDimension(String maxImageDeliveryDimension) {
        this.maxImageDeliveryDimension = maxImageDeliveryDimension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSuppliedFilename() {
        return suppliedFilename;
    }

    public void setSuppliedFilename(String suppliedFilename) {
        this.suppliedFilename = suppliedFilename;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(String processingDate) {
        this.processingDate = processingDate;
    }


}
