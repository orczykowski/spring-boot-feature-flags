package io.github.orczykowski.springbootfeatureflags.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Serves the feature flags admin panel as a single HTML page.
 * CSS and JS are maintained as separate classpath resources and inlined at startup.
 * Enabled when {@code feature-flags.enabled}, {@code feature-flags.api.manage.enabled},
 * and {@code feature-flags.admin-panel.enabled} are all set to {@code true}.
 */
@Controller
@ConditionalOnExpression(
        "${feature-flags.enabled:false} " +
        "and ${feature-flags.api.manage.enabled:false} " +
        "and ${feature-flags.admin-panel.enabled:false}")
class FeatureFlagAdminPanelController {

    private final String htmlContent;

    FeatureFlagAdminPanelController(
            @Value("${feature-flags.api.manage.path:/manage/feature-flags}") final String managePath,
            @Value("${feature-flags.admin-panel.logo-url:}") final String logoUrl) {
        this.htmlContent = loadAndPrepareHtml(managePath, logoUrl);
    }

    @GetMapping(
            path = "${feature-flags.admin-panel.path:/feature-flags-admin}",
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    String adminPanel() {
        return htmlContent;
    }

    private static String loadAndPrepareHtml(final String managePath, final String logoUrl) {
        try {
            var html = loadClasspathResource("feature-flags-admin.html");
            var css = loadClasspathResource("feature-flags-admin.css");
            var js = loadClasspathResource("feature-flags-admin.js");
            var resolvedLogoUrl = resolveLogoUrl(logoUrl);
            return html
                    .replace("{{INLINE_CSS}}", css)
                    .replace("{{INLINE_JS}}", js)
                    .replace("{{MANAGE_API_BASE_PATH}}", managePath)
                    .replace("{{LOGO_URL}}", resolvedLogoUrl);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load feature-flags admin panel resources from classpath", e);
        }
    }

    private static String resolveLogoUrl(final String logoUrl) throws IOException {
        if (logoUrl != null && !logoUrl.isBlank()) {
            return logoUrl;
        }
        var svg = loadClasspathResource("logo.svg");
        return "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
    }

    private static String loadClasspathResource(final String name) throws IOException {
        return new ClassPathResource(name).getContentAsString(StandardCharsets.UTF_8);
    }
}
