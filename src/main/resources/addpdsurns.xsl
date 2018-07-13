<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs mods"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:col="http://api.lib.harvard.edu/v2/collection/"
    xmlns:lc="http://hul.harvard.edu/ois/xml/ns/libraryCloud" version="1.0">

    <xsl:output encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:param name="param1"><results/></xsl:param>
    <!--<xsl:param name="param1"><results><image><url>http://nrs.harvard.edu/urn-3:HUL.ARCH:34375684</url><thumb>https://ids.lib.harvard.edu/ids/iiif/436213610/full/,150/0/native.jpg</thumb></image><image><url>http://nrs.harvard.edu/urn-3:HUL.ARCH:34375688</url><thumb>https://ids.lib.harvard.edu/ids/iiif/436213614/full/,150/0/native.jpg</thumb></image></results></xsl:param>-->

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="mods:location[mods:url/@access='raw object']">
        <xsl:copy>
            <xsl:apply-templates select="mods:url[@access='raw object']"/>
            <xsl:variable name="rawobj"><xsl:value-of select="mods:url[@access = 'raw object']"/></xsl:variable>
            <xsl:variable name="results" select="$param1"/>
            <!-- Assumes that PDS thumbnails should NOT
                 overwrite or replace or be appended to existing
                 thumbnails -->
            <xsl:choose>
              <xsl:when test="string-length(mods:url[@access='preview']) &gt; 0">
                <xsl:apply-templates select="mods:url[@access='preview']" />
              </xsl:when>
              <xsl:when test="string-length($results//image[url = $rawobj]/thumb) &gt; 0">
                <xsl:element name="url" namespace="http://www.loc.gov/mods/v3">
                  <xsl:attribute name="access">
                    <xsl:text>preview</xsl:text>
                  </xsl:attribute>
                  <xsl:value-of select="$results//image[url = $rawobj]/thumb"/>
                </xsl:element>
              </xsl:when>
              <xsl:otherwise>
                <xsl:comment>PDSThumbsProcessor was unable to find a thumbnail for this resource</xsl:comment>
              </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
