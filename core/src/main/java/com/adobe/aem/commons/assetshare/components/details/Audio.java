package com.adobe.aem.commons.assetshare.components.details;

import org.osgi.annotation.versioning.ProviderType;

/**
 *
 * Interface for Audio Component
 *
 */
@ProviderType
public interface Audio extends EmptyTextComponent {
    /**
     * @return Returns src
     */
    String getSrc();

    /**
     * @return Returns true if the asset's mime type is audio
     */
    boolean isAudioAsset();
}
