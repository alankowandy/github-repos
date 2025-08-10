package com.example.github_repos.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepo(String name, Owner owner, boolean fork) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Owner(String login) {}
}
