<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs mods librarycloud xlink"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:librarycloud="http://hul.harvard.edu/ois/xml/ns/librarycloud"
    version="2.0"
    >

    <xsl:output indent="no" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xsl:param name="param1" select='node()'/>

    <xsl:template match="@*|node()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="mods:modsCollection">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="mods:mods">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*" />
            <extension xmlns="http://www.loc.gov/mods/v3">
                <librarycloud:originalDocument>
                  <xsl:value-of select="concat($param1//marcpath,./mods:recordInfo/mods:recordIdentifier)"/>
                </librarycloud:originalDocument>
            </extension>
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>
