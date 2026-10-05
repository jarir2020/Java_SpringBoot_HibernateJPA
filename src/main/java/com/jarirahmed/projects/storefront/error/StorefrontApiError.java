package com.jarirahmed.projects.storefront.error;

import java.util.Map;

public record StorefrontApiError(int status, String message, Map<String, String> fieldErrors) {
}
