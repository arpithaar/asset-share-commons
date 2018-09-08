package com.adobe.aem.commons.assetshare.components.details.impl;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Required;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.osgi.service.component.annotations.Reference;

import com.adobe.aem.commons.assetshare.components.details.Audio;
import com.adobe.aem.commons.assetshare.content.AssetModel;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.DamUtil;

/**
 *
 * Sling Model for Audio Component
 *
 */
@Model(
        adaptables = { SlingHttpServletRequest.class },
        adapters = { Audio.class },
        resourceType = { AudioImpl.RESOURCE_TYPE },
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AudioImpl extends AbstractEmptyTextComponent implements Audio {

    protected static final String RESOURCE_TYPE = "asset-share-commons/components/details/audio";

    @Self
    @Required
    private AssetModel assetModel;

    @ValueMapValue
    private String computedProperty;

    @ValueMapValue
    private String renditionRegex;

    private ValueMap combinedProperties;

    private String src = null;

    @PostConstruct
    public void init() {
        if (assetModel != null) {
            combinedProperties = assetModel.getProperties();
        }
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isBlank(getSrc());
    }

    @Override
    public boolean isReady() {
        return !isEmpty();
    }

    @Override
    public boolean isAudioAsset() {
        if (null != assetModel && null != assetModel.getResource()) {
            final Asset asset = assetModel.getResource().adaptTo(Asset.class);
            if (asset.getMetadataValue("dc:format").startsWith("audio")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getSrc() {
        if (src == null) {
            src = combinedProperties.get(computedProperty, String.class);
            if (StringUtils.isBlank(src) && StringUtils.isNotBlank(renditionRegex)) {
                fetchSrcFromRegex();
            }
        }
        return src;
    }

    /**
     * Method fetches the rendition path from regex
     */
    private void fetchSrcFromRegex() {
        final Pattern pattern = Pattern.compile(renditionRegex);
        for (final Rendition rendition : assetModel.getRenditions()) {
            if (!"audio/x-flv".equalsIgnoreCase(rendition.getMimeType())
                    && pattern.matcher(rendition.getName()).matches()) {
                src = rendition.getPath();
                break;
            }
        }
    }

}
