<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:col="http://api.lib.harvard.edu/v2/collection/"
    xmlns:lc="http://hul.harvard.edu/ois/xml/ns/libraryCloud"
    xmlns:sets="http://hul.harvard.edu/ois/xml/ns/sets"
    exclude-result-prefixes="xs xlink mods dcterms dc col lc sets"
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

    <!-- Remove existing collections; changed set prefix/ns, continue to handle legacy "lc:" -->
    <xsl:template match="mods:extension[lc:sets]"/>
    <xsl:template match="mods:extension[sets:sets]"/>
    <!-- remove empty locations, sets -->
    <xsl:template match="mods:location[not(./*)]"/>
    <xsl:template match="mods:extension[sets:sets[not(./*)]]"/>

    <xsl:template match="mods:mods">
        <xsl:copy>
            <xsl:apply-templates select="*[not(local-name() = 'recordInfo')]"/>
            <xsl:variable name="collections" select="$param1"/>
            <!--<xsl:variable name="holdings" select="document('')//xsl:param[@name='param1']//holdings"/>-->
            <xsl:variable name="recordid">
                <xsl:value-of select="./mods:recordInfo/mods:recordIdentifier"/>
            </xsl:variable>
            <xsl:element name="extension" xmlns="http://www.loc.gov/mods/v3">
                <sets:sets>
                     <xsl:for-each
                        select="$collections//col:item[col:item_id = $recordid]/col:collections">
                         <sets:set>
                            <sets:systemId>
                                <xsl:value-of select="col:systemId"/>
                            </sets:systemId>
                            <sets:setName>
                                <xsl:value-of select="col:setName"/>
                            </sets:setName>
                            <sets:setSpec>
                                <xsl:value-of select="col:setSpec"/>
                            </sets:setSpec>
                            <sets:baseUrl>
                              <xsl:value-of select="col:baseUrl"/>
                            </sets:baseUrl>
                         </sets:set>
                    </xsl:for-each>
                </sets:sets>
            </xsl:element>
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
    <xsl:template match="mods:url[@access='object in context' and @displayLabel!='Harvard Digital Collections'] and @displayLabel!='Harvard Art Museums']">
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
                <xsl:value-of select="col:baseUrl" /><xsl:value-of select="$recordid" />
            </url>
          </xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
