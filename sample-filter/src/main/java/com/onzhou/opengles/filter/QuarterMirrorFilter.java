package com.onzhou.opengles.filter;

import com.onzhou.opengles.utils.ShaderReaderUtil;

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public class QuarterMirrorFilter extends BaseFilter {

    public QuarterMirrorFilter() {
        super(ShaderReaderUtil.INSTANCE.readResource(R.raw.quarter_mirror_filter_vertex_shader), ShaderReaderUtil.INSTANCE.readResource(R.raw.quarter_mirror_filter_fragment_shader));
    }


}
