<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3" 
    xmlns:usage="http://lib.harvard.edu/usagedata" version="2.0"
    >
    
    <xsl:param name="param1"><recordIdList/></xsl:param>
   
   
    <xsl:template match="@*|node()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="mods:modsCollection">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    

    
        <xsl:template match="mods:mods">
        
        <xsl:variable name="restrictedRecordId"><xsl:value-of select="./mods:recordInfo/mods:recordIdentifier" /></xsl:variable>
        <xsl:variable name="restrictedRecord"><xsl:value-of select="./mods" /></xsl:variable>
        <xsl:variable name="recordIdList" select="$param1" />
        <xsl:variable name="hits" select="count($recordIdList/recordIdList/recordId[text() = $restrictedRecordId])" />
        <xsl:if test="$hits = 0">
	  <xsl:copy-of select="."/>
        </xsl:if>
<!--	 <xsl:for-each select="$recordIdList/recordId = $restrictedRecordId">
	   <xsl:if test="not(position()=1)">
            <xsl:copy-of select="$restrictedRecord"/>
	</xsl:if>
	  
	  </xsl:for-each>-->
        
    </xsl:template>

    
</xsl:stylesheet>    