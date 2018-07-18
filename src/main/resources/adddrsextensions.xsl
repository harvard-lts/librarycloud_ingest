<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs mods"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:urlinfo="http://lib.harvard.edu/urlinfo"
    xmlns:HarvardDRS="http://hul.harvard.edu/ois/xml/ns/HarvardDRS" version="2.0">

    <xsl:output encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:param name="param1"><results/></xsl:param>


    <xsl:template match="@* | node()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="mods:modsCollection">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="returnQualifiedUrls">
        <xsl:param name="node" />
        <xsl:for-each select="$node/descendant::mods:url[@access = 'raw object' and not(contains(.,'HUL.FIG')) and not(contains(.,'ebookbatch')) and not(contains(.,'ejournals')) and not(contains(.,'HUL.gisdata'))]">
            <url><xsl:value-of select="." /></url>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="mods:mods">
        <xsl:variable name="qualifiedUrls">
            <xsl:call-template name="returnQualifiedUrls">
                <xsl:with-param name="node" select="." />
            </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="count($qualifiedUrls/url) != 1 or ./mods:typeOfResource/@collection">
                <xsl:copy-of select="."/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="*"/>
                    <xsl:variable name="results" select="$param1"/>
                    <xsl:variable name="urn">
                        <xsl:choose>
                            <xsl:when test="contains($qualifiedUrls/url/text(), '?')">
                                <xsl:value-of
                                    select="substring-before(substring-after($qualifiedUrls/url/text(), 'urn-3'), '?')"
                                />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of
                                    select="substring-after($qualifiedUrls/url/text(), 'urn-3')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:apply-templates
                        select="$results//docs[lower-case(substring-after(urn, 'urn-3')) = lower-case($urn)]"/>
                    <xsl:if test="mods:recordInfo/mods:recordIdentifier/@source = 'MH:ALEPH'">
                        <xsl:element name="relatedItem" namespace="http://www.loc.gov/mods/v3">
                            <xsl:attribute name="otherType">HOLLIS record</xsl:attribute>
                            <xsl:element name="location" namespace="http://www.loc.gov/mods/v3">
                                <xsl:element name="url" namespace="http://www.loc.gov/mods/v3">
                                    <xsl:text>http://id.lib.harvard.edu/aleph/</xsl:text>
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
                                </xsl:element>
                            </xsl:element>
                        </xsl:element>
                    </xsl:if>
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
            <xsl:if test="../../mods:location/mods:url[@access = 'raw object']/@displayLabel[not(.='Full Image')]">
                <xsl:text>, </xsl:text>
                <xsl:value-of
                    select="../../mods:location/mods:url[@access = 'raw object']/@displayLabel"/>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="docs">
        <xsl:element name="extension" namespace="http://www.loc.gov/mods/v3">
            <xsl:element name="HarvardDRS:DRSMetadata"
                xmlns:HarvardDRS="http://hul.harvard.edu/ois/xml/ns/HarvardDRS">
                <!--<xsl:apply-templates select="inDRS"/>-->
                <xsl:element name="inDRS" namespace="http://hul.harvard.edu/ois/xml/ns/HarvardDRS">
                    <xsl:text>true</xsl:text>
                </xsl:element>
                <xsl:apply-templates select="accessFlag"/>
                <xsl:apply-templates select="contentModel"/>
                <xsl:apply-templates select="uriType"/>
                <xsl:apply-templates select="fileDeliveryURL[not(. = '')]"/>
                <xsl:apply-templates select="ownerCode"/>
                <xsl:apply-templates select="ownerCodeDisplayName[not(. = '')]"/>
                <xsl:apply-templates select="metsLabel[not(. = '')]"/>
                <xsl:apply-templates select="lastModifiedDate[not(. = '')]"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="mods:location">
        <xsl:variable name="qualifiedUrls">
            <xsl:call-template name="returnQualifiedUrls">
                <xsl:with-param name="node" select="." />
            </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="count($qualifiedUrls/url) != 1">
                <xsl:copy>
                    <xsl:apply-templates select="*" />
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="*"/>
                    <xsl:variable name="results" select="$param1"/>
                    <xsl:variable name="urn">
                        <xsl:choose>
                            <xsl:when test="contains($qualifiedUrls/url, '?')">
                                <xsl:value-of select="substring-before(substring-after($qualifiedUrls/url, 'urn-3'), '?')" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of
                                    select="substring-after($qualifiedUrls/url, 'urn-3')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:choose>
                        <xsl:when test="not(string-length(mods:url[@access = 'preview']))">
                            <xsl:apply-templates
                                select="$results//docs[lower-case(substring-after(urn, 'urn-3')) = lower-case($urn)]/thumbnailURL[not(. = '') and not(. = 'null')]"/>
                        </xsl:when>
                        <xsl:otherwise/>
                    </xsl:choose>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="mods:url[@access='preview']">
      <xsl:if test="not(. = '')">
        <xsl:copy-of select="." />
      </xsl:if>
    </xsl:template>

    <xsl:template match="thumbnailURL">
        <xsl:element name="url" namespace="http://www.loc.gov/mods/v3">
            <xsl:attribute name="access">
                <xsl:text>preview</xsl:text>
            </xsl:attribute>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="inDRS">
        <xsl:element name="inDRS" namespace="http://hul.harvard.edu/ois/xml/ns/HarvardDRS">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="accessFlag">
        <xsl:element name="accessFlag" namespace="http://hul.harvard.edu/ois/xml/ns/HarvardDRS">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="uriType">
        <xsl:element name="uriType" namespace="http://hul.harvard.edu/ois/xml/ns/HarvardDRS">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="fileDeliveryURL">
        <xsl:element name="fileDeliveryURL" namespace="http://hul.harvard.edu/ois/xml/ns/HarvardDRS">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="contentModel">
        <xsl:element name="contentModel" namespace="http://hul.harvard.edu/ois/xml/ns/HarvardDRS">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="ownerCode">
        <xsl:element name="ownerCode" namespace="http://hul.harvard.edu/ois/xml/ns/HarvardDRS">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>


    <xsl:template match="ownerCodeDisplayName">
        <xsl:element name="ownerCodeDisplayName"
            namespace="http://hul.harvard.edu/ois/xml/ns/HarvardDRS">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="metsLabel">
        <xsl:element name="metsLabel" namespace="http://hul.harvard.edu/ois/xml/ns/HarvardDRS">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="lastModifiedDate">
        <xsl:element name="lastModifiedDate"
            namespace="http://hul.harvard.edu/ois/xml/ns/HarvardDRS">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
