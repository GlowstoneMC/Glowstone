package net.glowstone.advancement;

import lombok.Data;

import java.util.Date;

@Data
public class CriterionProgress {

    private final boolean achieved;
    private final Date time;

}
