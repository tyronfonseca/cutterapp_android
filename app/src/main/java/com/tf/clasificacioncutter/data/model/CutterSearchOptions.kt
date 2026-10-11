package com.tf.clasificacioncutter.data.model

import com.tf.clasificacioncutter.data.settings.IAppSettings

class CutterSearchOptions(
    settings: IAppSettings,
    dontSeparateName: Boolean? = null,
    ignoreArticles: Boolean? = null,
    useFormatMac: Boolean? = null
) {
    val dontSeparateName: Boolean = dontSeparateName ?: settings.dontSeparateName
    val ignoreArticles: Boolean = ignoreArticles ?: settings.ignoreArticles
    val useFormatMac: Boolean = useFormatMac ?: settings.useFormatMac
}
