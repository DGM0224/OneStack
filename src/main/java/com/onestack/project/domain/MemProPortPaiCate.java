package com.onestack.project.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemProPortPaiCate {
	private Member member;
	private Professional professional;
	private Portfolio portfolio;
	private Category category;
	private ProfessionalAdvancedInformation professionalAdvancedInformation;
}
