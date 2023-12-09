package com.dev.mythiccore.stats.elemental_stat;

import io.lumine.mythic.lib.element.Element;

public enum ASTElementStatType {
    PERCENT("Percent Damage"),
    DAMAGE_BONUS("Damage Bonus (%)"),
    RESISTANCE("Resistance (%)");

    private final String name;

    ASTElementStatType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String lowerCaseName() {
        return this.name().toLowerCase().replace("_", "-");
    }

    public String getConcatenatedTagPath(Element element) {
        return element.getId() + "_" + this.name();
    }

    public String getConcatenatedConfigPath(Element element) {
        return element.getId().toLowerCase().replace("_", "-") + "." + this.name().toLowerCase().replace("_", "-");
    }
}
