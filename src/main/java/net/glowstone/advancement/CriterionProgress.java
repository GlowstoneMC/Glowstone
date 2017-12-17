package net.glowstone.advancement;

import java.util.Date;
import lombok.Data;

@Data
public class CriterionProgress {

    private final boolean achieved;
    private final Date time;

}
