<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:marc="http://www.loc.gov/MARC21/slim"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="xml" encoding="UTF-8"/>
    <xsl:template match="//marc:collection">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <!-- Remove Cassidy records and those marked as suppressed -->
    <xsl:template match="marc:record">
        <xsl:if test="not(marc:datafield[@tag=040]/marc:subfield[@code='a']='CASSD')
                      and not(marc:datafield[@tag=950]/marc:subfield[@code='e']='true')">
            <xsl:copy-of select="."/>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>