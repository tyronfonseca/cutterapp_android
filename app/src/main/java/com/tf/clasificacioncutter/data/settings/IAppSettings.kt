package com.tf.clasificacioncutter.data.settings

import com.tf.clasificacioncutter.data.model.TextBeforeAfterNum

interface IAppSettings {
    var dontSeparateName: Boolean
    var ignoreArticles: Boolean
    var textBeforeNum: TextBeforeAfterNum
    var charsBeforeNum: Int
    var textAfterNum: TextBeforeAfterNum
    var charsAfterNum: Int
    var includeExtrasInExport: Boolean
    var useFormatMac: Boolean
}