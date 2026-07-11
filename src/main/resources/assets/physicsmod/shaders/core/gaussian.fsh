#version 330

in vec2 pass_textureCoords;

out vec4 out_color;

layout(std140) uniform PhysicsLiquidGaussianBlur {
	vec4 physics_blurOffsetTexel;
	vec4 physics_blurCamera;
	int physics_zZeroToOne;
	int physics_reverseZ;
};

uniform sampler2D imageMap;

float screenSpaceRadius(float depth, int imageWidth) {
	return abs((imageWidth * physics_blurCamera.z) / (2.0 * depth));
}

float linearEyeDepth(float depth) {
	float near = physics_blurCamera.x;
	float far  = physics_blurCamera.y;

	float z = depth;

	if (physics_zZeroToOne == 0) {
		z = depth * 2.0 - 1.0;
	}

	float nearNdc = physics_reverseZ == 1
		? 1.0
		: (physics_zZeroToOne == 1 ? 0.0 : -1.0);

	float farNdc = physics_reverseZ == 1
		? (physics_zZeroToOne == 1 ? 0.0 : -1.0)
		: 1.0;

	float invNear = 1.0 / near;

	// Handles finite far and reverse-Z infinite far.
	float invFar = far > 1.0e20 ? 0.0 : 1.0 / far;

	float b = (nearNdc - farNdc) / (invNear - invFar);
	float a = nearNdc - b * invNear;

	return b / (z - a);
}

float gaussianKernel(int x, float sigma) {
	float c = 2.0 * sigma * sigma;
	return exp(-float(x * x) / c);
}

void gaussian1DBlur(vec2 axisOffset, float depthCenter, inout float depthBlurred, inout float kernelAmount) {
	int imageWidth = textureSize(imageMap, 0).x;
	float depthCenterLinear = linearEyeDepth(depthCenter);
	float depthStrength = 6.0;
	int maxRadius = imageWidth / 10;

	float screenRadius = screenSpaceRadius(depthCenterLinear, imageWidth) * physics_blurCamera.w;
	int radius = min(maxRadius, int(round(screenRadius)));
	float radiusFraction = max(0.0, float(radius) - screenRadius);
	float sigma = max(0.0000001, (float(radius) - radiusFraction) / 6.0);

	for (int i = -radius; i <= radius; i++) {
		vec2 texelOffset = axisOffset * physics_blurOffsetTexel.zw * float(i);
		float depthOffset = texture(imageMap, pass_textureCoords + texelOffset).r;
		float depthOffsetLinear = linearEyeDepth(depthOffset);
		float depthDiff = depthCenterLinear - depthOffsetLinear;
		float weightDepth = exp(-(depthDiff * depthDiff * depthStrength));
		float kernelWeight = gaussianKernel(i, sigma);
		float weight = kernelWeight * weightDepth;
		depthBlurred += depthOffset * weight;
		kernelAmount += weight;
	}
}

void main() {
	vec4 source = texture(imageMap, pass_textureCoords);
	float depthCenter = source.r;
	float depthBlurred = 0.0;
	float kernelAmount = 0.0;

	if (depthCenter > 0.0) {
		gaussian1DBlur(physics_blurOffsetTexel.xy, depthCenter, depthBlurred, kernelAmount);
	}
		
	if (kernelAmount == 0.0) {
		out_color = source;
	} else {
		out_color = vec4(depthBlurred / kernelAmount, source.g, source.b, source.a);
	}
}
