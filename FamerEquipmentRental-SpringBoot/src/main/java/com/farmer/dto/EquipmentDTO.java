package com.farmer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDTO {

	private Long id;
	private Integer category;
	private Integer farm_type;
	private Integer quality;
	private String latitude;
	private String longitude;

}
