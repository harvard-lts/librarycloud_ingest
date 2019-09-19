<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:HarvardDRS="http://hul.harvard.edu/ois/xml/ns/HarvardDRS"
    exclude-result-prefixes="xs mods xlink HarvardDRS" version="2.0">

    <xsl:output encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:param name="param1">
        <results/>
    </xsl:param>


    <xsl:template match="@* | node()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="mods:modsCollection">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="returnQualifiedUrls">
        <xsl:param name="node"/>
        <xsl:for-each
            select="$node/descendant::mods:url[@access = 'raw object' and not(contains(., 'HUL.FIG')) and not(contains(., 'ebookbatch')) and not(contains(., 'ejournals')) and not(contains(., 'HUL.gisdata')) and not(contains(., 'hul.gisdata'))]">
            <url>
                <xsl:value-of select="."/>
            </url>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="mods:mods">
        <xsl:variable name="qualifiedUrls">
            <xsl:call-template name="returnQualifiedUrls">
                <xsl:with-param name="node" select="."/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
            <xsl:when
                test="(count($qualifiedUrls/url) != 1 or ./mods:typeOfResource/@collection) and not(mods:recordInfo/mods:recordIdentifier/@source = 'MH:MCZArtwork') and not(mods:recordInfo/mods:recordIdentifier/@source = 'MH:MHPL') and not(mods:recordInfo/mods:recordIdentifier/@source = 'MH:IOHP')">
                <xsl:copy-of select="."/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:apply-templates select="*"/>
                    <xsl:variable name="results" select="$param1"/>
                    <xsl:for-each select="$qualifiedUrls/url">
                        <xsl:variable name="urn">
                            <xsl:choose>
                                <xsl:when test="contains(./text(), '?')">
                                    <xsl:value-of
                                        select="substring-before(substring-after(./text(), 'urn-3'), '?')"
                                    />
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="substring-after(./text(), 'urn-3')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:apply-templates
                            select="$results//docs[lower-case(substring-after(urn, 'urn-3')) = lower-case($urn)]"
                        />
                    </xsl:for-each>
                    <!-- we are already doing this in marc 2 mods xform 
                    <xsl:if test="mods:recordInfo/mods:recordIdentifier/@source = 'MH:ALMA'">
                        <relatedItem xmlns="http://www.loc.gov/mods/v3" otherType="HOLLIS record">
                            <location xmlns="http://www.loc.gov/mods/v3">
                                <url xmlns="http://www.loc.gov/mods/v3">
                                    <xsl:text>https://id.lib.harvard.edu/alma/</xsl:text>
                                    <xsl:choose>
                                        <xsl:when
                                            test="contains(mods:recordInfo/mods:recordIdentifier, '_')">
                                            <xsl:value-of
                                                select="substring-before(mods:recordInfo/mods:recordIdentifier, '_')"
                                            />
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of
                                                select="mods:recordInfo/mods:recordIdentifier"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:text>/catalog</xsl:text>
                                </url>
                            </location>
                        </relatedItem>
                    </xsl:if>-->
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- append 856 subf 3 (mods:url/@displayLabel, if present, to split titles, so they can be distinguished -->

    <xsl:template match="mods:titleInfo">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="mods:title[@type]">
        <xsl:copy-of select="."/>
    </xsl:template>


    <xsl:template match="mods:title[not(@type)]">
        <xsl:copy>
            <xsl:value-of select="."/>
            <xsl:if
                test="../../mods:location/mods:url[@access = 'raw object']/@displayLabel[not(. = 'Full Image')]">
                <xsl:text>, </xsl:text>
                <xsl:value-of
                    select="../../mods:location/mods:url[@access = 'raw object']/@displayLabel"/>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="docs">
        <extension xmlns="http://www.loc.gov/mods/v3">
            <xsl:element name="HarvardDRS:DRSMetadata">
                <!--<xsl:apply-templates select="inDRS"/>-->
                <xsl:element name="HarvardDRS:inDRS">
                    <xsl:text>true</xsl:text>
                </xsl:element>
                <!--
                <xsl:apply-templates select="drsObjectId[not(. = 'null')]"/>
                <xsl:apply-templates select="accessFlag[not(. = 'null')]"/>
                <xsl:apply-templates select="contentModel[not(. = 'null')]"/>
                <xsl:apply-templates select="uriType[not(. = 'null')]"/>
                <xsl:apply-templates select="fileDeliveryURL[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="ownerCode[not(. = 'null')]"/>
                <xsl:apply-templates select="ownerCodeDisplayName[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="metsLabel[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="lastModifiedDate[not(. = '') and not(. = 'null')]"/>
                -->
                <xsl:apply-templates select="drsObjectId[not(. = 'null')]"/>
                <xsl:apply-templates select="drsFileId[not(. = 'null')]"/>
                <xsl:apply-templates select="accessFlag[not(. = 'null')]"/>
                <xsl:apply-templates select="contentModel[not(. = 'null')]"/>
                <xsl:apply-templates select="contentModelCode[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="uriType[not(. = 'null')]"/>
                <xsl:apply-templates select="fileDeliveryURL[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="ownerCode[not(. = 'null')]"/>
                <xsl:apply-templates select="ownerCodeDisplayName[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="metsLabel[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="lastModifiedDate[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="insertionDate[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="ownerSuppliedName[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="viewText[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="maxImageDeliveryDimension[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="mimeType[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="suppliedFilename[not(. = '') and not(. = 'null')]"/>
                <xsl:if test="harvardMetadataLink">
                    <xsl:element name="HarvardDRS:harvardMetadataLinks">
                        <xsl:apply-templates select="harvardMetadataLink[not(. = '') and not(. = 'null')]"/>
                    </xsl:element>                    
                </xsl:if>
            </xsl:element>
        </extension>
    </xsl:template>

    <xsl:template match="mods:location">
        <xsl:variable name="qualifiedUrls">
            <xsl:call-template name="returnQualifiedUrls">
                <xsl:with-param name="node" select="."/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="count($qualifiedUrls/url) != 1">
                <xsl:copy>
                    <xsl:apply-templates select="*"/>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="*"/>
                    <xsl:variable name="results" select="$param1"/>
                    <xsl:variable name="urn">
                        <xsl:choose>
                            <xsl:when test="contains($qualifiedUrls/url, '?')">
                                <xsl:value-of
                                    select="substring-before(substring-after($qualifiedUrls/url, 'urn-3'), '?')"
                                />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="substring-after($qualifiedUrls/url, 'urn-3')"
                                />
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:choose>
                        <xsl:when test="not(string-length(mods:url[@access = 'preview']))">
                            <xsl:apply-templates
                                select="$results//docs[lower-case(substring-after(urn, 'urn-3')) = lower-case($urn)]/thumbnailURL[not(. = '') and not(. = 'null')]"
                            />
                        </xsl:when>
                        <xsl:otherwise/>
                    </xsl:choose>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="mods:url[@access = 'preview']">
        <xsl:if test="not(. = '')">
            <xsl:copy-of select="."/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="thumbnailURL">
        <url xmlns="http://www.loc.gov/mods/v3">
            <xsl:attribute name="access">
                <xsl:text>preview</xsl:text>
            </xsl:attribute>
            <xsl:value-of select="."/>
        </url>
    </xsl:template>

    <xsl:template match="inDRS">
        <xsl:element name="HarvardDRS:inDRS">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="accessFlag">
        <xsl:element name="HarvardDRS:accessFlag">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="uriType">
        <xsl:element name="HarvardDRS:uriType">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="fileDeliveryURL">
        <xsl:element name="HarvardDRS:fileDeliveryURL">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="contentModel">
        <xsl:element name="HarvardDRS:contentModel">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="contentModelCode">
        <xsl:element name="HarvardDRS:contentModelCode">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="ownerSuppliedName">
        <xsl:element name="HarvardDRS:ownerSuppliedName">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="ownerCode">
        <xsl:element name="HarvardDRS:ownerCode">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="ownerCodeDisplayName">
        <xsl:element name="HarvardDRS:ownerCodeDisplayName">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="metsLabel">
        <xsl:element name="HarvardDRS:metsLabel">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="suppliedFilename">
        <xsl:element name="HarvardDRS:suppliedFilename">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="maxImageDeliveryDimension">
        <xsl:element name="HarvardDRS:maxImageDeliveryDimension">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="mimeType">
        <xsl:element name="HarvardDRS:mimeType">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="viewText">
        <xsl:element name="HarvardDRS:viewText">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="harvardMetadataLink">
        <xsl:element name="HarvardDRS:harvardMetadataLink">
        <xsl:for-each select="tokenize(., '~')">
            <xsl:variable name="pos" select="position()"/>
            <xsl:choose>
                <xsl:when test="$pos = 1">
                    <xsl:element name="HarvardDRS:metadataType">
                        <xsl:value-of select="."/>
                    </xsl:element>
                </xsl:when>
                <xsl:when test="$pos = 2">
                    <xsl:element name="HarvardDRS:metadataIdentifier">
                        <xsl:value-of select="."/>
                    </xsl:element>
                </xsl:when>
                <xsl:when test="$pos = 3">
                    <xsl:element name="HarvardDRS:displayLabel">
                        <xsl:value-of select="."/>
                    </xsl:element>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template match="lastModifiedDate">
        <xsl:element name="HarvardDRS:lastModifiedDate">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="insertionDate">
        <xsl:element name="HarvardDRS:insertionDate">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="drsObjectId">
        <xsl:element name="HarvardDRS:drsObjectId">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="drsFileId">
        <xsl:element name="HarvardDRS:drsFileId">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
