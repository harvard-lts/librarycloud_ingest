<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:marc="http://www.loc.gov/MARC21/slim">
    <xsl:output method="text" encoding="utf-8"/>
    <xsl:strip-space elements="*"/>
    <xsl:param name="break" select="'&#xA;'"/>

    <xsl:template match="marc:collection">
        <xsl:apply-templates select="marc:record"/>
    </xsl:template>

    <xsl:template match="marc:record">
        <xsl:apply-templates select="marc:controlfield[@tag = '001']"/>
    </xsl:template>

    <!-- When matching DataSeriesBodyType: do nothing -->
    <xsl:template match="marc:controlfield[@tag = '001']">
        <xsl:value-of select="."/>
        <xsl:value-of select="$break"/>
    </xsl:template>

</xsl:stylesheet>
