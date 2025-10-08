package com.insurance.app.catalog.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long policy_id;

    private String policyName;
    private String type;
    private double premium;
    private Integer tenure;
    private String coverage;
    private boolean active;

}
