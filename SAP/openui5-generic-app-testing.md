**Status**: **Failed**

**Confidence**: Max (10.0, max confidence value is 10.0)

## Violated rules

1.  **[rl-assigned_teams-1]** Does it have enough teams on GitHub? **No**
1.  **[rl-assigned_teams-2]** Does it have an admin team on GitHub? **No**
1.  **[rl-assigned_teams-3]** Does it have enough admins on GitHub? **No**






## Passed rules

1.  **[rl-assigned_teams-4]** Does it have a team with push privileges on GitHub? **Yes**
1.  **[rl-assigned_teams-5]** Does teams have enough members on GitHub? **Yes**
1.  **[rl-contributor_file-1]** Does it have a contributing guideline? **Yes**
1.  **[rl-contributor_file-2]** Does the contributing guideline have required text? **Yes**
1.  **[rl-license_file-1]** Does it have a license file? **Yes**
1.  **[rl-license_file-2]** Does it use an allowed license? **Yes**
1.  **[rl-license_file-3]** Does the license have disallowed content? **No**
1.  **[rl-readme_file-1]** Does it have a README file? **Yes**
1.  **[rl-reuse_tool-1]** Does README mention REUSE? **Yes**
1.  **[rl-reuse_tool-2]** Does it have LICENSES directory with licenses? **Yes**
1.  **[rl-reuse_tool-3]** Is it registered in REUSE? **Yes**
1.  **[rl-reuse_tool-4]** Is it compliant with REUSE rules? **Yes**
1.  **[rl-security_policy-1]** Does it have a security policy? **Yes**
1.  **[rl-vulnerability_alerts-1]** Are vulnerability alerts enabled? **Yes**
1.  **[rl-vulnerability_alerts-2]** Does it have unresolved vulnerability alerts? **No**


## How to fix it

1.  No team with administrative privileges for the given repository could be found. Please contact the OSPO so that such a team can be created for your repository.2.  An admins team could be found, but it only contains one person or is even empty. If there is a maintainer in the team, please ask this person to add another administrator or contact the OSPO to add the colleagues who are entitled to be admins.3.  Only one or no team could be found which is assigned. Please make sure to have at least one member and one admin team assigned to the repository. If you need assistance, please contact the OSPO.4.  In earlier times, SAP requested projects to add an API usage section to the LICENSE file. As the LICENSE file should only contain the native license text and the API section has moved to the dep5 file of the REUSE project information, we ask all projects to remove the API section from the LICENSE file.