package edu.harvard.libcomm.model;

import java.util.List;
import javax.persistence.*;

@Entity
@Table(name="checksum")
public class Checksum  {

	public Checksum() {}

	@Id @GeneratedValue
	private int id;

	@Column(nullable = false, unique=true)
	private String itemId;

	@Column(nullable = false)
	private Integer checksum;

	public int getId() {
		return id;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Integer getChecksum(){
		return checksum;
	} 

	public void setChecksum(Integer checksum){
		this.checksum = checksum;
	}

}