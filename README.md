# FF4GDX

An FFmpeg subsystem for libGDX

## PRE-RELEASE NOTICE

[NB: As of time of writing, not only is FF4GDX pre-release, but it is incomplete. The library is NOT finished, there are no downloads - I am still in progress of merging working code from private working experiments so there will also be a lot of missig documentation or un-optimized code]

This is PRE-RELEASE SOFTWARE. Note that this is not "alpha" or "beta", it is designed to be functional and stable - so bug reports ARE WELCOME. All kinds of testers and code reviewers and hackers of all experience levels are encouraged to get in touch. Please see Contributing section on how you can help :)

If you want to learn more about using the library, reading this entire readme is highly encouraged. I have, however, strived to document as much code as possible - so feel free to just jump right in and take a look at `FF4GDX` for how to use the lib, or `FF4GDXPlayer` for a reference implementation of a video player. 

## Introduction

FF4GDX provides the capability to use FFmpeg sources within a libGDX project. This includes:

- Streaming one or more video files to a texture (optionally with sound via miniaudio<sup>*1</sup>)

- TODO: Advanced stream capabilities (seeking, frame jumping, A-B repeat, playback speed, etc.)

- TODO: Loading of FFmpeg sources as simple assets (e.g. images or audio files)

- TODO: Using FFmpeg-sourced files as advanced assets, such as animated sprite sheets or audio atlases

- TODO: scene2d actors for video

- ...more? Suggestions welcome!

## Platform + Hardware support

- LWJGL3 (Win64 only, Linux TODO and Mac is very TODO)

- TODO: Android (requires extra work + sample code for LGPL compliance, see FAQ)

## Project Overview

FF4GDX provides a bridge between libGDX and FFmpeg, via javacpp-presets's JNI mappings. These JNI presets give us fairly low-level access to FFmpeg, so FF4GDX provides a nice abstractions for you to use.

FF4GDX consists of:

1) The main **Subsystem**, or **engine** if you prefer. This seems similar to a physics engine in that it must be ticked each frame. It is a state machine responsible for essential routines like high-resolution timekeeping and resource-provision to each FFsAsset;

2) The individual **FFsAsset**(s) which act as references to each FFmpeg-sourced file;

3) Optionally, an actual libGDX **Asset** that represents an FFmpeg-source, or *part* of a source, which is always loaded in memory (rather than buffered/streamed from storage).

The subsystem is contained within the `FF4GDX`class which must be created during the *launcher* of your GDX project, then `start`ed during creation, then additionally `tick`ed and `render`ed every render call. Additionally it requires the same `resize`, `pause`, `resume` and `dispose`  to be called from your GDX activity. These are all static methods, their purpose being to help faciliate resource management when streaming multiple sources.

`FFsAsset` is an abstract class, used by 

## FAQ

##### Android support?

FF4GDX includes FFmpeg, which is an LGPLv3 build. All non-free and GPL decoders are disabled, though AV1 decoding is provided via libdav1d since it is a royalty-free format. Android has two specific issues related to this situation:

1) It does not support a lot of formats. In the case for e.g. h264, it's up to your hardware to provide it. For Desktop, this is easy since we can just use free Nvidia/AMD/Intel API's to do HW-only decoding, avoiding the need for your to technically pay for a h264 playback license or whatever it is (IANAL) because your GPU provider already paid that. I don't know if this works on Android.

2) Being LGPL, the software needs to provide capability to replace FFmpeg libraries with another version. This is easily provided on desktop because the libraries are in a plain folder and can simply be overwritten with different files. On Android however, an in-app facility is required to comply with this. I will not support Android until I have the reference player working with Android and the ability to load different FFmpeg natives. 

...these issues are not difficult to solve, just time consuming. And I'd rather work on the core project more for the moment. It is somewhat moderate on my mental priority, however.

##### Mac support?

I believe I need an actuall installation of MacOSX in order to build the natives. Either that, or I can just use stock java-presets build which won't include av1 decoder, and possibly other things... but even then I can't test it personally.
