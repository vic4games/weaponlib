#version 120

uniform sampler2D DiffuseSampler;
uniform float Radius;

varying vec2 texCoord;
varying vec2 oneTexel;

//const float RADIUS = 0.55;

const float SOFTNESS = 0.25;

void main(){
    vec4 texColor = texture2D(DiffuseSampler, texCoord.xy);

    float dist = distance(texCoord.xy, vec2(0.5,0.5));
    float vignette = smoothstep(Radius, Radius - SOFTNESS, dist);
    texColor *= vignette;

    gl_FragColor = vec4(texColor.rgb, 1);
}
