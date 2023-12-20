#include <iostream>

class AbstractModulator
{
public:
    virtual double output(double t);  
};

class ASKModulator 
{
private:
    // modulation depth (0, 1]
    // amplitude of carrier
    // frequency of carrier
    // frequency of shifting
    double depth, amplitude, fCarrier, fShift;
    int frame;

public:
    double output(double t);

    ASKModulator(double depth, double amplitude, double fCarrier, double fShift) 
    {   
        if (depth <= 0 || depth > 1)
        {
            fprintf(stderr, "depth %f is out of range (0, 1]", depth);
            exit(-1);
        }

        this->depth = depth;
        this->amplitude = amplitude;
        this->fCarrier = fCarrier;
        this->fShift = fShift;
    }
};