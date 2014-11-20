<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3" version="1.0">
    
    <xsl:output indent="yes" method="text"/>
    
    <xsl:template match="mods:modsCollection">
        <xsl:element name="add">
            <xsl:apply-templates select="mods:mods/mods:recordInfo/mods:recordIdentifier"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="mods:recordIdentifier">
        <xsl:value-of select="."/><xsl:if test="not(position()=last())"><xsl:text>,</xsl:text></xsl:if>
    </xsl:template>
    
    <xsl:template match="*"/>
    
</xsl:stylesheet>