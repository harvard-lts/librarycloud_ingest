<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs mods"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3" 
    xmlns:usage="http://lib.harvard.edu/usagedata" version="1.0"
    >
    
    <xsl:output indent="no"/>
    <xsl:strip-space elements="*"/>
 
    <xsl:param name="param1"><stackscore/></xsl:param>
   
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
            <xsl:apply-templates select="*" />
            <xsl:variable name="stackscore" select="$param1"/> 
            <xsl:variable name="hollisid"><xsl:value-of select="./mods:recordInfo/mods:recordIdentifier"/></xsl:variable>
            
            <xsl:for-each select="$stackscore//docs[id_inst=$hollisid]">
                <xsl:if test="position()=1">
                    <xsl:element name="extension" namespace="http://www.loc.gov/mods/v3">
                            <xsl:if test="$stackscore//docs[id_inst=$hollisid]/shelfrank">
                                <xsl:element name="usageData" namespace="http://lib.harvard.edu/usagedata">
                                    <xsl:element name="stackScore" namespace="http://lib.harvard.edu/usagedata">
                                        <xsl:value-of select="$stackscore//docs[id_inst=$hollisid]/shelfrank"/>
                                    </xsl:element>
                                </xsl:element>
                            </xsl:if>
                    </xsl:element>
                </xsl:if>
            </xsl:for-each>
            
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>    