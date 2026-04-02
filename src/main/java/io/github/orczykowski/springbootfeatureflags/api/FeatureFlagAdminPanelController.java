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
            @Value("${feature-flags.api.manage.path:/manage/feature-flags}") final String managePath) {
        this.htmlContent = loadAndPrepareHtml(managePath);
    }

    @GetMapping(
            path = "${feature-flags.admin-panel.path:/feature-flags-admin}",
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    String adminPanel() {
        return htmlContent;
    }

    private static String loadAndPrepareHtml(final String managePath) {
        try {
            var html = loadClasspathResource("feature-flags-admin.html");
            var css = loadClasspathResource("feature-flags-admin.css");
            var js = loadClasspathResource("feature-flags-admin.js");
            return html
                    .replace("{{INLINE_CSS}}", css)
                    .replace("{{INLINE_JS}}", js)
                    .replace("{{MANAGE_API_BASE_PATH}}", managePath);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load feature-flags admin panel resources from classpath", e);
        }
    }

    private static String loadClasspathResource(final String name) throws IOException {
        return new ClassPathResource(name).getContentAsString(StandardCharsets.UTF_8);
    }
}
