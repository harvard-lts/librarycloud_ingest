<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3" version="1.0">

    <xsl:output indent="yes" method="text"/>

    <xsl:template match="/">
        <xsl:element name="add">
          <xsl:for-each select="//mods:url[@access='raw object' and contains(.,'urn-3') and not(contains(.,'HUL.FIG')) and not(contains(.,'ebookbatch')) and not(contains(.,'ejournals'))]">
              <xsl:text>%22</xsl:text>
              <xsl:choose>
                <xsl:when test="contains(.,'?')">
                  <xsl:value-of select="substring-before(concat('urn-3',substring-after(.,'urn-3')),'?')"/><!--<xsl:text>,</xsl:text>-->
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="concat('urn-3',substring-after(.,'urn-3'))"/><!--<xsl:text>,</xsl:text>-->
                </xsl:otherwise>
              </xsl:choose>
              <xsl:text>%22</xsl:text>
              <xsl:if test="position() != last()">
                <xsl:text> OR </xsl:text>
              </xsl:if>
          </xsl:for-each>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
