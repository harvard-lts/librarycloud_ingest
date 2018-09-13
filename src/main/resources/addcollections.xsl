<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:col="http://api.lib.harvard.edu/v2/collection/"
    xmlns:lc="http://hul.harvard.edu/ois/xml/ns/libraryCloud"
    exclude-result-prefixes="xs xlink mods dcterms dc col lc"
    version="2.0">

    <xsl:output encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:param name="param1"></xsl:param>

    <xsl:template match="@* | node()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="mods:modsCollection">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <!-- Remove existing collections -->
    <xsl:template match="mods:extension[lc:sets]"/>

    <xsl:template match="mods:mods">
        <xsl:copy>
            <xsl:apply-templates select="*[not(local-name() = 'recordInfo')]"/>
            <xsl:variable name="collections" select="$param1"/>
            <!--<xsl:variable name="holdings" select="document('')//xsl:param[@name='param1']//holdings"/>-->
            <xsl:variable name="recordid">
                <xsl:value-of select="./mods:recordInfo/mods:recordIdentifier"/>
            </xsl:variable>
            <extension xmlns="http://www.loc.gov/mods/v3">
                <lc:sets>
                     <xsl:for-each
                        select="$collections//col:item[col:item_id = $recordid]/col:collections">
                         <lc:set>
                            <lc:systemId>
                                <xsl:value-of select="col:systemId"/>
                            </lc:systemId>
                            <lc:setName>
                                <xsl:value-of select="col:setName"/>
                            </lc:setName>
                            <lc:setSpec>
                                <xsl:value-of select="col:setSpec"/>
                            </lc:setSpec>
                            <lc:baseUrl>
                              <xsl:value-of select="col:baseUrl"/>
                            </lc:baseUrl>
                         </lc:set>
                    </xsl:for-each>
                </lc:sets>
            </extension>
            <xsl:apply-templates select="mods:recordInfo"/>
            <xsl:if test="count(mods:location/mods:url) &lt; 1">
              <location xmlns="http://www.loc.gov/mods/v3">
                <xsl:call-template name="object-in-context-links">
                  <xsl:with-param name="modsRoot" select="." />
                </xsl:call-template>
              </location>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="mods:location[mods:url][1]">
      <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates />
        <xsl:if test="local-name(..) = 'mods'">
          <xsl:call-template name="object-in-context-links">
            <xsl:with-param name="modsRoot" select="ancestor::mods:mods" />
          </xsl:call-template>
        </xsl:if>
      </xsl:copy>
    </xsl:template>

    <!-- abandon existing spotlight links-->
    <xsl:template match="mods:url[@access='object in context' and @displayLabel!='Harvard Digital Collections']">
    </xsl:template>

    <xsl:template name="object-in-context-links">
      <xsl:param name="modsRoot" />
      <xsl:variable name="collections" select="$param1"/>
      <xsl:variable name="recordid">
        <xsl:value-of select="$modsRoot/mods:recordInfo/mods:recordIdentifier"/>
      </xsl:variable>
      <xsl:for-each select="$collections//col:item[col:item_id = $recordid]/col:collections">
          <xsl:if test="string-length(normalize-space(col:baseUrl))">
            <url xmlns="http://www.loc.gov/mods/v3" access="object in context">
                <xsl:attribute name="displayLabel">
                    <xsl:value-of select="col:setName/text()" />
                </xsl:attribute>
                <xsl:value-of select="col:baseUrl" />-<xsl:value-of select="$recordid" />
            </url>
          </xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
