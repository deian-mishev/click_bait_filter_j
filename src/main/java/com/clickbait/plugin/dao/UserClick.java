package com.clickbait.plugin.dao;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.Past;

import com.clickbait.plugin.annotations.SQLInjectionSafe;
import com.clickbait.plugin.annotations.UUIDA;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserClick {

    @UUIDA
    @JsonProperty("userId")
    private final UUID userId;

    @SQLInjectionSafe
    @JsonProperty("domain")
    private final String domain;

    @SQLInjectionSafe
    @JsonProperty("link")
    private final String link;

    @Past
    @JsonProperty("atTime")
    private final LocalDate atTime;

    @JsonProperty("score")
    private final Float score;

    public UserClick(UUID userId, String domain, String link, LocalDate atTime, Float score) {
        this.userId = userId;
        this.score = score;
        this.domain = domain;
        this.link = link;
        this.atTime = atTime;
    }

    public UserClick(UUID userId, String link, LocalDate atTime) {
        this.atTime = atTime;
        this.userId = userId;
        this.link = link;
        this.domain = null;
        this.score = null;
    }

    public UserClick(String link, Float score) {
        this.score = score;
        this.link = link;
        this.userId = null;
        this.domain = null;
        this.atTime = null;
    }

    public UserClick() {
        this.score = null;
        this.userId = null;
        this.domain = null;
        this.link = null;
        this.atTime = null;
    }

    public Float getScore() {
        return score;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getDomain() {
        return domain;
    }

    public String getLink() {
        return link;
    }

    public LocalDate getAtTime() {
        return atTime;
    }

    @Override
    public String toString() {
        return "UserClick{userId=" + userId + ", domain=" + domain + ", link=" + link + ", atTime=" + atTime + "}";
    }
}
