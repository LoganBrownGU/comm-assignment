class AbstractDatastream 
{
public:
    virtual char nextbyte(); 
};

class GIFStream : public AbstractDatastream 
{
public:
    char nextbyte();
private:
    
};