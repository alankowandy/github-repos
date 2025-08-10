package com.example.github_repos.dto;

import java.util.List;

public record RepoDto(String name, String ownerLogin, List<BranchDto> branches) {}
