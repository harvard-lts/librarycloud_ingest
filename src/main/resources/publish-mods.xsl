<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:tbd="http://lib.harvard.edu/TBD"
    xmlns:HarvardRepositories="http://hul.harvard.edu/ois/xml/ns/HarvardRepositories"
    xmlns:processingDate="http://hul.harvard.edu/ois/xml/ns/processingDate"
    xmlns:availableTo="http://hul.harvard.edu/ois/xml/ns/availableTo"
    xmlns:digitalFormats="http://hul.harvard.edu/ois/xml/ns/digitalFormats"
    xmlns:HarvardDRS="http://hul.harvard.edu/ois/xml/ns/HarvardDRS"
    xmlns:countries="info:lc/xmlns/codelist-v1"
    xmlns:sets="http://hul.harvard.edu/ois/xml/ns/libraryCloud"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    exclude-result-prefixes="mods xs tbd xlink HarvardRepositories processingDate availableTo digitalFormats HarvardDRS xsi countries"
    version="2.0">
    <!-- <xsl:namespace-alias stylesheet-prefix="mods" result-prefix="" /> -->
    <xsl:output method="xml" encoding="UTF-8"/>
    <xsl:param name="param1"><processingDate/></xsl:param>
    <xsl:param name="repository-map-file" select="'src/main/resources/RepositoryNameMapping.xml'" />
    <xsl:variable name="map" select="document($repository-map-file)" />

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
          <xsl:if test="mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'AUDIO']">
            <format>Audio</format>
          </xsl:if>
          <xsl:if test="mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'DOCUMENT'] or mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'PDS DOCUMENT'] or mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'PDS DOCUMENT LIST'] or mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'TEXT']">
            <format>Books and documents</format>
          </xsl:if>
          <xsl:if test="mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'VIDEO']">
            <format>Video </format>
          </xsl:if>
          <xsl:if test="mods:extension/HarvardDRS:DRSMetadata[HarvardDRS:contentModel = 'STILL IMAGE']">
            <format>Images</format>
          </xsl:if>
        </xsl:variable>

        <xsl:variable name="harvardRepositoriesMap">
            <xsl:variable name="locations" select="mods:location/mods:physicalLocation[@type = 'repository']" />
            <xsl:for-each select="$map//mapping">
                <xsl:variable name="source" select="./source" />
                <xsl:if test="$locations[text() = $source]">
                    <xsl:copy-of select="." />
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
            <xsl:otherwise></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:if test="not(mods:recordInfo/mods:recordOrigin='Open Metadata Status: RES-C') and not(mods:recordInfo/mods:recordOrigin='Open Metadata Status: RES-D')">
            <xsl:copy>
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates />
                <xsl:if test="count($digitalFormats/format) &gt; 0">
                    <extension xmlns="http://www.loc.gov/mods/v3">
                        <digitalFormats:digitalFormats>
                            <xsl:for-each select="$digitalFormats/format">
                                <digitalFormats:digitalFormat>
                                    <xsl:value-of select="." />
                                </digitalFormats:digitalFormat>
                            </xsl:for-each>
                        </digitalFormats:digitalFormats>
                    </extension>
                </xsl:if>

                <xsl:if test="string-length($availableTo)">
                    <extension xmlns="http://www.loc.gov/mods/v3">
                        <availableTo:availableTo>
                            <xsl:value-of select="$availableTo" />
                        </availableTo:availableTo>
                    </extension>
                </xsl:if>

                <extension xmlns="http://www.loc.gov/mods/v3">
                    <xsl:if test="count($harvardRepositoriesMap/mapping) &gt; 0">
                        <HarvardRepositories:HarvardRepositories>
                            <xsl:for-each select="$harvardRepositoriesMap/mapping">
                                <HarvardRepositories:HarvardRepository>
                                    <xsl:value-of select="./extensionValue" />
                                </HarvardRepositories:HarvardRepository>
                            </xsl:for-each>
                        </HarvardRepositories:HarvardRepositories>
                    </xsl:if>
                </extension>

                <extension xmlns="http://www.loc.gov/mods/v3">
                    <processingDate:processingDate>
                        <xsl:value-of select="$param1" />
                    </processingDate:processingDate>
                </extension>

            </xsl:copy>
        </xsl:if>

    </xsl:template>

    <xsl:template match="mods:location[mods:url][1]">
        <xsl:copy>
            <xsl:copy-of select="@* | node()"/>
            <xsl:if test="//mods:extension/HarvardDRS:DRSMetadata/HarvardDRS:accessFlag = 'P'">
                <url xmlns="http://www.loc.gov/mods/v3" access="object in context" displayLabel="Harvard Digital Collections">http://id.lib.harvard.edu/digital_collections/<xsl:value-of select="//mods:recordInfo/mods:recordIdentifier" /></url>
            </xsl:if>
            <xsl:for-each select="//mods:extension//sets:set">
                <url xmlns="http://www.loc.gov/mods/v3" access="object in context">
                    <xsl:attribute name="displayLabel">
                        <xsl:value-of select="sets:setName/text()" />
                    </xsl:attribute>
                    <xsl:value-of select="./sets:baseUrl" />-<xsl:value-of select="//mods:recordInfo/mods:recordIdentifier" />
                </url>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="mods:location/mods:physicalLocation">
        <xsl:variable name="source" select="./text()" />
        <xsl:choose>
            <xsl:when test="@type = 'repository'">
                <xsl:copy>
                    <xsl:for-each select="@*">
                        <xsl:choose>
                            <xsl:when test="local-name() = 'displayLabel' and string-length($map//mapping[source=$source]/replacement)"></xsl:when>
                            <xsl:when test="local-name() = 'valueURI' and string-length($map//mapping[source=$source]/valueURI)"></xsl:when>
                            <xsl:otherwise><xsl:apply-templates select="." /></xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                    <xsl:if test="string-length($map//mapping[source=$source]/replacement)">
                        <xsl:attribute name="displayLabel">Harvard repository</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length($map//mapping[source=$source]/valueURI)">
                        <xsl:attribute name="valueURI">
                            <xsl:value-of select="$map//mapping[source=$source]/valueURI" />
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:choose>
                        <xsl:when test="string-length($map//mapping[source=$source]/replacement) &gt; 0">
                            <xsl:value-of select="$map//mapping[source=$source]/replacement" />
                        </xsl:when>
                        <xsl:otherwise><xsl:value-of select="text()" /></xsl:otherwise>
                    </xsl:choose>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="." />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="@* | *">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
