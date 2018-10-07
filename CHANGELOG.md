# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased] - YYYY-MM-DD
### Added
* `POST /users`: Creates a new user
    * Request body:

        ```json
        {
            "externalId": "id-of-the-user-inside-my-application"
        }
        ```
    * Responses:
        * `201`: User was successfully created

            ```json
            {
                "user": {
                    "id": 0,
                    "externalId": "user-xyz"
                }
            }
            ```

### Changed
(changes in existing functionality)

### Deprecated
(changes that will be removed in an upcoming release)

### Removed
(removed features)

### Fixed
(fixed bugs)

### Security
(security fixes)

### Maintenance
(other changes, that are only interesting for developers of this project)



# Release entry template
## [Unreleased] - YYYY-MM-DD
### Added
(new features)

### Changed
(changes in existing functionality)

### Deprecated
(changes that will be removed in an upcoming release)

### Removed
(removed features)

### Fixed
(fixed bugs)

### Security
(security fixes)

### Maintenance
(other changes, that are only interesting for developers of this project)