package com.marcn.mediathek.model.zdf;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.marcn.mediathek.model.base.Asset;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = ZdfAsset.UnknownZdfAsset.class)

@JsonSubTypes({
        @JsonSubTypes.Type(value = ZdfStation.class, name = "sender"),
        @JsonSubTypes.Type(value = ZdfSeries.class, name = "sendung"),
        @JsonSubTypes.Type(value = ZdfEpisode.class, name = "video"),
        @JsonSubTypes.Type(value = ZdfEpisode.class, name = "einzelsendung"),
        @JsonSubTypes.Type(value = ZdfTheme.class, name = "thema"),
        @JsonSubTypes.Type(value = ZdfRubric.class, name = "rubrik"),
        @JsonSubTypes.Type(value = ZdfLive.class, name = "livevideo"),
        @JsonSubTypes.Type(value = ZdfLive.class, name = "livestream")
})
public interface ZdfAsset extends Asset {
    class UnknownZdfAsset {
    }
}
