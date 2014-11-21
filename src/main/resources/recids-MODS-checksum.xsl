<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3" version="1.0"
    xmlns:libCloud="items.libCloud.org"><!-- a real value should go here-->
    
    <xsl:output indent="yes" method="text"/>
    
    <xsl:template match="mods:modsCollection">
        <xsl:element name="add">
            <xsl:apply-templates select="mods:mods" />
        </xsl:element>
    </xsl:template>

    <xsl:template match="mods:mods">
            <xsl:value-of select="./mods:recordInfo/mods:recordIdentifier"/><xsl:text>,</xsl:text>        
            <xsl:value-of select="libCloud:checksum(.)" /><xsl:if test="not(position()=last())"><xsl:text>|</xsl:text></xsl:if>
    </xsl:template>

    
    <xsl:template match="*"/>
    
    
     <xsl:function name="libCloud:checksum" as="xs:integer">
        <xsl:param name="str" as="xs:string"/>
        <xsl:variable name="codepoints" select="string-to-codepoints($str)"/>
        <xsl:value-of select="libCloud:hash($codepoints, count($codepoints), floor(1), 0, 0)"/>
    </xsl:function>

    <!-- can I change some xs:integers to xs:int and help performance? -->
    <xsl:function name="libCloud:hash">
        <xsl:param name="str" as="xs:integer*"/>
        <xsl:param name="len"  />
        <xsl:param name="index"  />
        <xsl:param name="sum1"  />
        <xsl:param name="sum2" />
        <xsl:choose>
            <xsl:when test="$index gt $len">
                <xsl:sequence select="floor($sum2 * 256 + $sum1)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="newSum1" 
                    select="floor(($sum1 + $str[$index]) mod 255)"/>
                <xsl:sequence select="libCloud:hash($str, $len, floor($index + 1), $newSum1,
                        ($sum2 + $newSum1) mod 255)" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
</xsl:stylesheet>