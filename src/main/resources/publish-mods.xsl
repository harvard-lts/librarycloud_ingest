<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:originalDocument="http://hul.harvard.edu/ois/xml/ns/originalDocument"
    xmlns:HarvardRepositories="http://hul.harvard.edu/ois/xml/ns/HarvardRepositories"
    xmlns:processingDate="http://hul.harvard.edu/ois/xml/ns/processingDate"
    xmlns:availableTo="http://hul.harvard.edu/ois/xml/ns/availableTo"
    xmlns:digitalFormats="http://hul.harvard.edu/ois/xml/ns/digitalFormats"
    xmlns:HarvardDRS="http://hul.harvard.edu/ois/xml/ns/HarvardDRS"
    xmlns:librarycloud="http://hul.harvard.edu/ois/xml/ns/librarycloud"
    xmlns:countries="info:lc/xmlns/codelist-v1" xmlns:sets="http://hul.harvard.edu/ois/xml/ns/sets"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-6.xsd"
    exclude-result-prefixes="mods xs sets originalDocument xlink HarvardRepositories processingDate availableTo digitalFormats HarvardDRS xsi countries librarycloud"
    version="2.0">
    <!-- <xsl:namespace-alias stylesheet-prefix="mods" result-prefix="" /> -->
    <xsl:output method="xml" encoding="UTF-8"/>
    <xsl:param name="param1">
        <processingDate/>
    </xsl:param>
    <xsl:param name="repository-map-file" select="'src/main/resources/RepositoryNameMapping.xml'"/>
    <xsl:variable name="map" select="document($repository-map-file)"/>

    <xsl:template match="mods:modsCollection">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="mods:mods">
        <!--
        <xsl:variable name="restrictedRec">
            <xsl:value-of select=".[not(contains(recordInfo/recordOrigin[starts-with(.,'Open Metadata')],'RES-C')) and not(contains(recordInfo/recordOrigin[starts-with(.,'Open Metadata')],'RES-D'))]"></xsl:value-of>
        </xsl:variable>
        -->
        <xsl:variable name="digitalFormats">
            <xsl:if
                test="mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'AUDIO'] or (mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'TEXT'] and mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:uriType = 'SDS'])">
                <format>Audio</format>
            </xsl:if>
            <xsl:if
                test="mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'DOCUMENT'] or mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'PDS DOCUMENT'] or mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'PDS DOCUMENT LIST'] or (mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'TEXT'] and not(mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:uriType = 'SDS']))">
                <format>Books and documents</format>
            </xsl:if>
            <xsl:if test="mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'VIDEO']">
                <format>Video </format>
            </xsl:if>
            <xsl:if
                test="mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'STILL IMAGE']">
                <format>Images</format>
            </xsl:if>
        </xsl:variable>

        <xsl:variable name="harvardRepositoriesMap">
            <xsl:variable name="locations"
                select="mods:location/mods:physicalLocation[@type = 'repository']"/>
            <xsl:for-each select="$map//mapping">
                <xsl:variable name="source" select="normalize-space(./source)"/>
                <xsl:if test="$locations[text() = $source]">
                    <xsl:copy-of select="."/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>

        <xsl:variable name="availableTo">
            <xsl:choose>
                <xsl:when test="mods:extension/HarvardDRS:DRSMetadata/HarvardDRS:accessFlag = 'R'">
                    <xsl:text>Restricted</xsl:text>
                </xsl:when>
                <xsl:when test="mods:extension/HarvardDRS:DRSMetadata/HarvardDRS:accessFlag = 'P'">
                    <xsl:text>Everyone</xsl:text>
                </xsl:when>
                <xsl:otherwise/>
            </xsl:choose>
        </xsl:variable>

        <!--<xsl:if
            test="not(mods:recordInfo/mods:recordOrigin = 'Open Metadata Status: RES-C') and not(mods:recordInfo/mods:recordOrigin = 'Open Metadata Status: RES-D')">-->
        <xsl:if
            test="not(lower-case(mods:recordInfo[mods:recordIdentifier/@source = 'MH:ALMA']/mods:recordContentSource[not(. = '')]) = 'uk-cbpil')">
            <xsl:copy>
                <xsl:copy-of select="document('')/*/@xsi:schemaLocation"/>
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates/>
                <xsl:choose>
                    <xsl:when test="mods:extension[librarycloud:librarycloud]">
                        <!--<xsl:apply-templates select="mods:extension[librarycloud:librarycloud]"></xsl:apply-templates>-->
                        <mods:extension>
                            <librarycloud:librarycloud>
                                <xsl:copy-of select="mods:extension/librarycloud:librarycloud/*"/>
                                <xsl:if
                                    test="not(mods:extension/librarycloud:librarycloud/librarycloud:availableTo)">
                                    <xsl:if test="string-length($availableTo)">
                                        <librarycloud:availableTo>
                                            <xsl:value-of select="$availableTo"/>
                                        </librarycloud:availableTo>

                                    </xsl:if>
                                </xsl:if>
                                <xsl:if
                                    test="not(mods:extension/librarycloud:librarycloud/librarycloud:digitalFormats)">
                                    <xsl:if test="count($digitalFormats/format) &gt; 0">
                                        <librarycloud:digitalFormats>
                                            <xsl:for-each select="$digitalFormats/format">
                                                <librarycloud:digitalFormat>
                                                  <xsl:value-of select="."/>
                                                </librarycloud:digitalFormat>
                                            </xsl:for-each>
                                        </librarycloud:digitalFormats>
                                    </xsl:if>
                                </xsl:if>
                            </librarycloud:librarycloud>
                        </mods:extension>
                    </xsl:when>
                    <xsl:otherwise>
                        <mods:extension>
                            <librarycloud:librarycloud>
                                <xsl:if test="string-length($availableTo)">
                                    <librarycloud:availableTo>
                                        <xsl:value-of select="$availableTo"/>
                                    </librarycloud:availableTo>
                                </xsl:if>
                                <xsl:if test="count($digitalFormats/format) &gt; 0">
                                    <librarycloud:digitalFormats>
                                        <xsl:for-each select="$digitalFormats/format">
                                            <librarycloud:digitalFormat>
                                                <xsl:value-of select="."/>
                                            </librarycloud:digitalFormat>
                                        </xsl:for-each>
                                    </librarycloud:digitalFormats>
                                </xsl:if>

                                <xsl:if test="count($harvardRepositoriesMap/mapping) &gt; 0">
                                    <librarycloud:HarvardRepositories>
                                        <xsl:for-each select="$harvardRepositoriesMap/mapping">
                                            <librarycloud:HarvardRepository>
                                                <xsl:value-of select="./extensionValue"/>
                                            </librarycloud:HarvardRepository>
                                        </xsl:for-each>
                                    </librarycloud:HarvardRepositories>
                                </xsl:if>
                                <xsl:apply-templates
                                    select="mods:extension/librarycloud:originalDocument"
                                    mode="lcloudchunk"/>
                                <xsl:apply-templates
                                    select="mods:extension/librarycloud:priorrecordids"
                                    mode="lcloudchunk"/>
                                <librarycloud:processingDate>
                                    <xsl:value-of select="$param1"/>
                                </librarycloud:processingDate>
                            </librarycloud:librarycloud>
                        </mods:extension>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="count(mods:location/mods:url) &lt; 1">
                    <location xmlns="http://www.loc.gov/mods/v3">
                        <xsl:call-template name="object-in-context-links">
                            <xsl:with-param name="modsRoot" select="."/>
                        </xsl:call-template>
                    </location>
                </xsl:if>
            </xsl:copy>
        </xsl:if>

    </xsl:template>

    <xsl:template match="mods:relatedItem">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
            <xsl:if test="string-length(mods:location/mods:physicalLocation[@type = 'repository'])">
                <xsl:element name="extension" namespace="http://www.loc.gov/mods/v3">
                    <xsl:for-each select="mods:location/mods:physicalLocation[@type = 'repository']">
                        <xsl:variable name="source" select="normalize-space(./text())"/>

                        <xsl:if test="string-length($map//mapping[normalize-space(source) = $source]/extensionValue)">
                            <librarycloud:librarycloud>
                                <librarycloud:HarvardRepositories>
                                    <librarycloud:HarvardRepository>
                                        <xsl:value-of
                                            select="$map//mapping[normalize-space(source) = $source]/extensionValue"
                                        />
                                    </librarycloud:HarvardRepository>
                                </librarycloud:HarvardRepositories>
                            </librarycloud:librarycloud>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:element>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="mods:location[mods:url][1]">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
            <xsl:if test="local-name(..) = 'mods'">
                <xsl:call-template name="object-in-context-links">
                    <xsl:with-param name="modsRoot" select="ancestor::mods:mods"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="mods:url">
        <xsl:choose>
            <xsl:when test="contains(., 'urn-3') or contains(., 'ids.lib.harvard.edu')">
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:value-of select="replace(., 'http:', 'https:')"/>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="contains(., 'URN-3') or contains(., 'ids.lib.harvard.edu')">
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:value-of select="replace(., 'http:', 'https:')"/>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="mods:location/mods:physicalLocation">
        <xsl:variable name="source" select="normalize-space(./text())"/>
        <xsl:choose>
            <xsl:when test="@type = 'repository'">
                <xsl:copy>
                    <xsl:for-each select="@*">
                        <xsl:choose>
                            <xsl:when
                                test="local-name() = 'displayLabel' and string-length($map//mapping[normalize-space(source) = $source]/replacement)"/>
                            <xsl:when
                                test="local-name() = 'valueURI' and string-length($map//mapping[normalize-space(source) = $source]/valueURI)"/>
                            <xsl:otherwise>
                                <xsl:apply-templates select="."/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                    <xsl:if test="string-length($map//mapping[normalize-space(source) = $source]/replacement)">
                        <xsl:attribute name="displayLabel">Harvard repository</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length($map//mapping[normalize-space(source) = $source]/valueURI)">
                        <xsl:attribute name="valueURI">
                            <xsl:value-of select="$map//mapping[normalize-space(source) = $source]/valueURI"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:choose>
                        <xsl:when
                            test="string-length($map//mapping[normalize-space(source) = $source]/replacement) &gt; 0">
                            <xsl:value-of select="$map//mapping[normalize-space(source) = $source]/replacement"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="text()"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="@* | *">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="object-in-context-links">
        <xsl:param name="modsRoot"/>
        <xsl:if
            test="$modsRoot/mods:extension/HarvardDRS:DRSMetadata/HarvardDRS:accessFlag = 'P' and not(mods:url[@displayLabel = 'Harvard Digital Collections'])">
            <url xmlns="http://www.loc.gov/mods/v3" access="object in context"
                displayLabel="Harvard Digital Collections"
                    >https://id.lib.harvard.edu/digital_collections/<xsl:value-of
                    select="$modsRoot/mods:recordInfo/mods:recordIdentifier"/></url>
        </xsl:if>
        <!-- HAM recs must also point back to the HAM site -->
        <xsl:if
            test="$modsRoot/mods:location/mods:physicalLocation[@type = 'repository'] = 'Harvard Art Museums'">
            <xsl:if test="$modsRoot/mods:identifier[@type = 'Object Number']">
                <url xmlns="http://www.loc.gov/mods/v3" access="object in context"
                    displayLabel="Harvard Art Museums"
                        >https://www.harvardartmuseums.org/collections/object/<xsl:value-of
                        select="$modsRoot/mods:identifier[@type = 'Object Number']"/></url>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <xsl:template match="HarvardDRS:fileDeliveryURL">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="starts-with(., 'http:')">
                    <xsl:value-of select="replace(., 'http:', 'https:')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>

    <!-- hide these, we are now pulling them in as part of one extension for librarycloud namespace elements -->
    <xsl:template
        match="mods:extension[librarycloud:priorrecordids] | mods:extension[librarycloud:originalDocument]"/>

    <xsl:template match="librarycloud:priorrecordids | librarycloud:originalDocument"
        mode="lcloudchunk">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="mods:extension[librarycloud:librarycloud]"/>
    <!--<xsl:template match="mods:extension[not(normalize-space() = '')]"/>-->

</xsl:stylesheet>
