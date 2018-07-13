<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:HarvardDRS="http://hul.harvard.edu/ois/xml/ns/HarvardDRS"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="xml" encoding="UTF-8"/>
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
            <xsl:value-of select=".[not(contains(mods:recordInfo/mods:recordOrigin[starts-with(.,'Open Metadata')],'RES-C')) and not(contains(mods:recordInfo/mods:recordOrigin[starts-with(.,'Open Metadata')],'RES-D'))]"></xsl:value-of>
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
                  <xsl:element name="extension" namespace="http://www.loc.gov/mods/v3">
                    <xsl:element name="digitalFormats" namespace="http://lib.harvard.edu/TBD">
                      <xsl:for-each select="$digitalFormats/format">
                        <xsl:element name="digitalFormat" namespace="http://lib.harvard.edu/TBD">
                          <xsl:value-of select="." />
                        </xsl:element>
                      </xsl:for-each>
                    </xsl:element>
                  </xsl:element>
                </xsl:if>

                <xsl:if test="string-length($availableTo)">
                  <xsl:element name="extension" namespace="http://www.loc.gov/mods/v3">
                    <xsl:element name="availableTo" namespace="http://lib.harvard.edu/TBD">
                      <xsl:value-of select="$availableTo" />
                    </xsl:element>
                  </xsl:element>
                </xsl:if>

                <extension>
                    <xsl:if test="count($harvardRepositoriesMap/mapping) &gt; 0">
                        <xsl:element name="HarvardRepositories" namespace="http://lib.harvard.edu/TBD">
                            <xsl:for-each select="$harvardRepositoriesMap/mapping">
                    <!-- <xsl:for-each select="mods:location/mods:physicalLocation[@type = 'repository']"> -->
                    <!--   <xsl:variable name="source" select="./text()" /> -->
                      <!-- <xsl:if test="string-length($map//mapping[source=$source]/extensionValue) &gt; 0"> -->
                        <xsl:element name="HarvardRepository" namespace="http://lib.harvard.edu/TBD">
                          <xsl:value-of select="./extensionValue" />
                        </xsl:element>
                      <!-- </xsl:if> -->
                            </xsl:for-each>
                        </xsl:element>
                    </xsl:if>
                </extension>


            </xsl:copy>
        </xsl:if>

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
