<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
    version="2.0"
    xmlns:marc="http://www.loc.gov/MARC21/slim">
    
    <xsl:output indent="yes" encoding="UTF-8"/>
    
    <xsl:template match="marc:collection">
        <xsl:element name="add">
            <xsl:apply-templates select="marc:record"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="marc:record">
        <xsl:if test="marc:datafield[@tag='852']/marc:subfield[@code='b']=not('BIO','BRM','CEA','CFI','DEV','FOG','HIL','LIT','PSY','QUA','RCA','RRC','SFL','SKL','SOC','WAR')">
        <xsl:element name="doc">
            <xsl:apply-templates select="marc:controlfield" mode="indx"/>
            <xsl:apply-templates select="marc:datafield[@tag='LKR']" mode="indx"/>
            <!-- change tp 004 once the marc holdings export changes -->
            <!--<xsl:apply-templates select="marc:datafield[@tag='999']"/>-->

            <xsl:element name="field">
                <xsl:attribute name="name">
                    <xsl:text>originalMarc</xsl:text>
                </xsl:attribute>
                <xsl:text disable-output-escaping="yes">
                    &lt;![CDATA[
                </xsl:text>
                <!--<xsl:copy-of select="."/>-->
                <xsl:element name="record" namespace="http://www.loc.gov/MARC21/slim">
                    <xsl:apply-templates select="marc:leader" mode="orig"/>
                    <xsl:apply-templates select="marc:controlfield[@tag='001']" mode="orig"/>
                    <xsl:apply-templates select="marc:controlfield[@tag='003']" mode="orig"/>
                    <xsl:apply-templates select="marc:datafield[@tag='LKR']" mode="orig"/>
                    <xsl:apply-templates select="marc:controlfield[@tag='005']" mode="orig"/>
                    <xsl:apply-templates select="marc:controlfield[@tag='007']" mode="orig"/>
                    <xsl:apply-templates select="marc:controlfield[@tag='008']" mode="orig"/>
                    <xsl:apply-templates select="marc:datafield[not(starts-with(@tag,'H')) and not(starts-with(@tag,'L') )]" mode="orig"/>

                </xsl:element>
                <!--<xsl:copy>
                    <xsl:copy-of select="marc:leader"/>
                    <xsl:copy-of select="marc:controlfield[@tag='001']"/>
                    <xsl:apply-templates select="marc:controlfield[@tag='003']" mode="orig"/>
                    <xsl:apply-templates select="marc:datafield[@tag='LKR']" mode="orig"/>
                    <xsl:apply-templates select="marc:controlfield[@tag='005']" mode="orig"/>
                    <xsl:apply-templates select="marc:controlfield[@tag='007']" mode="orig"/>
                    <xsl:apply-templates select="marc:controlfield[@tag='008']" mode="orig"/>
                    <xsl:copy-of select="marc:datafield[not(starts-with(@tag,'H')) and not(starts-with(@tag,'L') )]"/>
                </xsl:copy>-->
                <xsl:text disable-output-escaping="yes">
                    ]]&gt;
                </xsl:text>
            </xsl:element>

        </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="marc:leader|marc:controlfield" mode="orig">
        <!--<xsl:copy-of select="."/>-->
        <xsl:element name="{local-name()}" namespace="http://www.loc.gov/MARC21/slim">
            <xsl:copy-of select="@* | node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="marc:datafield" mode="orig">
        <xsl:element name="{local-name()}" namespace="http://www.loc.gov/MARC21/slim">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="marc:subfield">
        <xsl:element name="{local-name()}" namespace="http://www.loc.gov/MARC21/slim">
            <xsl:copy-of select="@* | node()"/>
        </xsl:element>        
    </xsl:template>

    <xsl:template match="marc:datafield[@tag='LKR']" mode="orig">
        <xsl:element name="controlfield" namespace="http://www.loc.gov/MARC21/slim">
            <xsl:attribute name="tag"><xsl:text>004</xsl:text></xsl:attribute>
            <xsl:value-of select="marc:subfield[@code='b']"/>
        </xsl:element>
    </xsl:template>
   
    <xsl:template match="marc:controlfield" mode="indx">
        <xsl:apply-templates select=".[@tag='001']"/>
        <xsl:apply-templates select=".[@tag='004']"/>
        <!--
        <xsl:element name="field">
            <xsl:attribute name="name">
                <xsl:value-of select="@tag"/>
            </xsl:attribute>       
            <xsl:value-of select="."/>
        </xsl:element>-->
    </xsl:template>
    
    <xsl:template match="marc:controlfield[@tag='001']" mode="indx">
        <xsl:element name="field">
            <xsl:attribute name="name">
                <xsl:text>id</xsl:text>
            </xsl:attribute>       
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="marc:datafield[@tag='LKR']" mode="indx">
        <xsl:element name="field">
            <xsl:attribute name="name">
                <xsl:text>004</xsl:text>
            </xsl:attribute>       
            <xsl:value-of select="substring(.,string-length(.) - 8)"/>
        </xsl:element>
    </xsl:template>

    <!-- change to 004 when marc holdings export changes -->
    <!--
    <xsl:template match="marc:datafield[@tag='999']">
        <xsl:element name="field">
            <xsl:attribute name="name">
                <xsl:text>004</xsl:text>
            </xsl:attribute>
            <xsl:value-of select="marc:subfield[@code='b']"/>
        </xsl:element>
    </xsl:template>
    -->    
</xsl:stylesheet>