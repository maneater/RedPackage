/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#ifndef __EASYAR_SAMPLE_UTILITY_SIMPLERENDERER_H__
#define __EASYAR_SAMPLE_UTILITY_SIMPLERENDERER_H__

#include "easyar/matrix.hpp"
#include "Matrices.hpp"

namespace EasyAR {

    class Renderer {
    public:
        void init();

        void render(const Matrix44F &projectionMatrix, const Matrix44F &cameraview, Vec2F size);

        void onScroll(float xOff, float yOff);

    private:
        unsigned int program_box;
        int pos_coord_box;
        int pos_color_box;
        int pos_trans_box;
        int pos_proj_box;
        float xOff;
        float yOff;
        unsigned int vbo_coord_box;
        unsigned int vbo_color_box;
        unsigned int vbo_color_box_2;
        unsigned int vbo_faces_box;
        Matrix4 transMatrix4 = Matrix4();
    };

}
#endif
