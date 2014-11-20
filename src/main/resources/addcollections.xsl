<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs mods"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3" 
    xmlns:usage="http://lib.harvard.edu/usagedata" version="1.0"
    xmlns:ns2="http://purl.org/dc/terms" 
    xmlns:ns3="http://purl.org/dc/elements/1.1" 
    xmlns:ns4="http://purl.org/dc/terms/" 
    xmlns:ns5="http://purl.org/dc/elements/1.1/" 
    xmlns:ns6="http://api.lib.harvard.edu/v2/collection/"
    >
    
    <xsl:output indent="no"/>
    <xsl:strip-space elements="*"/>
 
    <xsl:param name="param1"><collections/></xsl:param> 
 
     
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
            <xsl:variable name="collections" select="$param1"/> 
            <xsl:variable name="hollisid"><xsl:value-of select="./mods:recordInfo/mods:recordIdentifier"/></xsl:variable>
            
            <xsl:for-each select="$collections//ns6:item[item_id=$hollisid]">
                <xsl:element name="extension" namespace="http://www.loc.gov/mods/v3">
                    <xsl:for-each select="$collections//ns6:item[item_id=$hollisid]/collections">
                        <xsl:element name="collection" namespace="http://api.lib.harvard.edu/v2/collection">
                            <xsl:element name="title" namespace="http://purl.org/dc/elements/1.1/">
                                <xsl:value-of select="ns5:title"/>
                            </xsl:element>
                            <xsl:element name="id" namespace="http://purl.org/dc/elements/1.1/">
                                <xsl:value-of select="ns3:identifier"/>
                            </xsl:element>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
            </xsl:for-each>
            
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>    