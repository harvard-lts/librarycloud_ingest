<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:mods="http://www.loc.gov/mods/v3"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:template match="mods:modsCollection">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="mods:mods">
        <!--
        <xsl:variable name="restrictedRec">
            <xsl:value-of select=".[not(contains(mods:recordInfo/mods:recordOrigin[starts-with(.,'Open Metadata')],'RES-C')) and not(contains(mods:recordInfo/mods:recordOrigin[starts-with(.,'Open Metadata')],'RES-D'))]"></xsl:value-of>
        </xsl:variable>
        -->
        <xsl:if test="not(mods:recordInfo/mods:recordOrigin='Open Metadata Status: RES-C') and not(mods:recordInfo/mods:recordOrigin='Open Metadata Status: RES-D')">
            <xsl:copy-of select="."/>            
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>