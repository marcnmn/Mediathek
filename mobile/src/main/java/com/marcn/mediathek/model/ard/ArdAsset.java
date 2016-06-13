package com.marcn.mediathek.model.ard;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.marcn.mediathek.model.base.Asset;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "teaserTyp",
        defaultImpl = ArdTeaser.class)

@JsonSubTypes({
        @JsonSubTypes.Type(value = ArdLive.class, name = "PermanentLivestreamClip"),
        @JsonSubTypes.Type(value = ArdLive.class, name = "Gruppe"),
        @JsonSubTypes.Type(value = ArdLive.class, name = "Sendung"),
})
public interface ArdAsset extends Asset {
    class UnknownZdfAsset {
    }
}
