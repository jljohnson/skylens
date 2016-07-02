Skylens
=======

Software used to determine if the airplane is either a Boeing 737 or an Airbus A320 using several neural networks.

Uses the [Neuroph](http://neuroph.sourceforge.net/) project.

This is a PoC. Use in production at your own will.

How does it work?
=================

Using three neural networks - `core`, `nos` and `krila` the program generates an output which determines what type is the aircraft.

Neural network `core` is used to determine if segments of `100x100` pixels are parts of the airplane or not. The airplane image before that is fed through a grayscale and Sobel filter.
The output is then fed into the `nos` and `krila` neural networks which determine do the parts belong to Boeing 737, according to a `THRESHOLD` constant (which can be changed; same with `CROP_SIZE` constant).

Mean values are used to determine if the result is above the threshold - if it is, then the plane in question is Boeing 737; if not, then it's an Airbus A320.

The code contains a bunch of loops for iteration over the picture, several lists to store resulting data and a basic loading form.

License
=======

MIT