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
            <xsl:when test="(count($qualifiedUrls/url) != 1 or ./mods:typeOfResource/@collection) and not(mods:recordInfo/mods:recordIdentifier/@source='MH:MCZArtwork') and not(mods:recordInfo/mods:recordIdentifier/@source='MH:MPCOL') and not(mods:recordInfo/mods:recordIdentifier/@source='MH:IOHP')">
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
                                <xsl:value-of
                                    select="substring-after(./text(), 'urn-3')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:apply-templates
                        select="$results//docs[lower-case(substring-after(urn, 'urn-3')) = lower-case($urn)]"/>
                    </xsl:for-each>
                    <xsl:if test="mods:recordInfo/mods:recordIdentifier/@source = 'MH:ALEPH'">
                        <relatedItem xmlns="http://www.loc.gov/mods/v3" otherType="HOLLIS record">
                            <location xmlns="http://www.loc.gov/mods/v3">
                                <url xmlns="http://www.loc.gov/mods/v3">
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
                                </url>
                            </location>
                        </relatedItem>
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
            <HarvardDRS:DRSMetadata>
                <!--<xsl:apply-templates select="inDRS"/>-->
                <HarvardDRS:inDRS>
                    <xsl:text>true</xsl:text>
                </HarvardDRS:inDRS>
                <xsl:apply-templates select="accessFlag[not(. = 'null')]"/>
                <xsl:apply-templates select="contentModel[not(. = 'null')]"/>
                <xsl:apply-templates select="uriType[not(. = 'null')]"/>
                <xsl:apply-templates select="fileDeliveryURL[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="ownerCode[not(. = 'null')]"/>
                <xsl:apply-templates select="ownerCodeDisplayName[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="metsLabel[not(. = '') and not(. = 'null')]"/>
                <xsl:apply-templates select="lastModifiedDate[not(. = '') and not(. = 'null')]"/>
            </HarvardDRS:DRSMetadata>
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
        <HarvardDRS:inDRS>
            <xsl:value-of select="."/>
        </HarvardDRS:inDRS>
    </xsl:template>

    <xsl:template match="accessFlag">
        <HarvardDRS:accessFlag>
            <xsl:value-of select="."/>
        </HarvardDRS:accessFlag>
    </xsl:template>

    <xsl:template match="uriType">
        <HarvardDRS:uriType>
            <xsl:value-of select="."/>
        </HarvardDRS:uriType>
    </xsl:template>

    <xsl:template match="fileDeliveryURL">
        <HarvardDRS:fileDeliveryURL>
            <xsl:value-of select="."/>
        </HarvardDRS:fileDeliveryURL>
    </xsl:template>

    <xsl:template match="contentModel">
        <HarvardDRS:contentModel>
            <xsl:value-of select="."/>
        </HarvardDRS:contentModel>
    </xsl:template>

    <xsl:template match="ownerCode">
        <HarvardDRS:ownerCode>
            <xsl:value-of select="."/>
        </HarvardDRS:ownerCode>
    </xsl:template>


    <xsl:template match="ownerCodeDisplayName">
        <HarvardDRS:ownerCodeDisplayName>
            <xsl:value-of select="."/>
        </HarvardDRS:ownerCodeDisplayName>
    </xsl:template>

    <xsl:template match="metsLabel">
        <HarvardDRS:metsLabel>
            <xsl:value-of select="."/>
        </HarvardDRS:metsLabel>
    </xsl:template>
    <xsl:template match="lastModifiedDate">
        <HarvardDRS:lastModifiedDate>
            <xsl:value-of select="."/>
        </HarvardDRS:lastModifiedDate>
    </xsl:template>

</xsl:stylesheet>
