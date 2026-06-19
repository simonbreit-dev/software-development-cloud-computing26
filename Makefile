# Makefile for OpenAPI code generation using Dockerized OpenAPI Generator CLI.
OPENAPI_IMAGE := openapitools/openapi-generator-cli:v7.22.0

# Root-level spec — single source of truth for the whole project
SPEC_FILE     := openApiSpec.yaml

# Generator config lives inside the backend module
CONFIG_FILE   := backend/api/generator-config.yaml

# Output target is the backend module root (generator writes into src/ from here)
OUTPUT_DIR    := backend/

# The generated sources — used by CI to detect drift
# Adjust the package path to match your actual Java package
GENERATED_DIR := backend/src/main/java/de/quadflal/sdfccbackend/adapter/in/web/generated

# -----------------------------------------------------------------------------
# Platform detection
# Ensures Docker writes files as the current user on Linux/Mac.
# On Windows (Git Bash / WSL) this is a no-op — Docker Desktop handles perms.
# -----------------------------------------------------------------------------
UNAME := $(shell uname 2>/dev/null || echo Windows)
ifneq ($(UNAME), Windows)
    USER_FLAG := --user $(shell id -u):$(shell id -g)
else
    USER_FLAG :=
endif

.PHONY: generate validate-spec check-generated help

# Default target
.DEFAULT_GOAL := help

## help: Show all available targets
help:
	@echo ""
	@echo "Usage: make <target>"
	@echo ""
	@grep -E '^## ' MAKEFILE | sed 's/## /  /'
	@echo ""

## generate: Generate Spring Boot API stubs from openApiSpec.yaml via Docker
generate:
	@echo ">>> Running OpenAPI generator..."
	docker run --rm \
		$(USER_FLAG) \
		-v "$(CURDIR):/workspace" \
		-w /workspace \
		$(OPENAPI_IMAGE) generate \
			-i /workspace/$(SPEC_FILE) \
			-c /workspace/$(CONFIG_FILE) \
			-o /workspace/$(OUTPUT_DIR)
	@echo ""
	@echo ">>> Done. Generated files written to: $(GENERATED_DIR)"
	@echo ""

## validate-spec: Validate openApiSpec.yaml without generating any code
validate-spec:
	@echo ">>> Validating $(SPEC_FILE)..."
	docker run --rm \
		-v "$(CURDIR):/workspace" \
		$(OPENAPI_IMAGE) validate \
			-i /workspace/$(SPEC_FILE)

## check-generated: [CI] Regenerate and fail if committed files differ from spec
## Blocks PRs where the spec changed but generated files were not updated,
## or where someone manually edited generated files.
check-generated: generate
	@echo ">>> Diffing generated files against committed state..."
	@if ! git diff --quiet -- $(GENERATED_DIR); then \
		echo ""; \
		echo "  Generated files are out of sync with $(SPEC_FILE)"; \
		echo ""; \
		echo "    Either the spec was changed without regenerating,"; \
		echo "    or generated files were edited manually."; \
		echo ""; \
		echo "    Fix: run 'make generate' locally and commit the result."; \
		echo ""; \
		git diff --stat -- $(GENERATED_DIR); \
		echo ""; \
		exit 1; \
	fi
	@echo "  Generated files are in sync with the spec."