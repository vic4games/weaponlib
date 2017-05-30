
package com.vicmatskiv.weaponlib.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Projectiles complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Projectiles">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="bleedingOnHit" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="destroyGlassBlocks" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Projectiles")
public class Projectiles {

    @XmlAttribute(name = "bleedingOnHit")
    protected Float bleedingOnHit;
    @XmlAttribute(name = "destroyGlassBlocks")
    protected Boolean destroyGlassBlocks;

    /**
     * Gets the value of the bleedingOnHit property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getBleedingOnHit() {
        return bleedingOnHit;
    }

    /**
     * Sets the value of the bleedingOnHit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setBleedingOnHit(Float value) {
        this.bleedingOnHit = value;
    }

    /**
     * Gets the value of the destroyGlassBlocks property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDestroyGlassBlocks() {
        return destroyGlassBlocks;
    }

    /**
     * Sets the value of the destroyGlassBlocks property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDestroyGlassBlocks(Boolean value) {
        this.destroyGlassBlocks = value;
    }

}
