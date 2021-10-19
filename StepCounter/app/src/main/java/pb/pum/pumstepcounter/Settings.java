package pb.pum.pumstepcounter;

public class Settings {
    private static Settings instance;

    int weightInKgs = 70;

    private Settings() {
    }

    public int getWeightInKgs() {
        return weightInKgs;
    }

    public void setWeightInKgs(int weight) {
        weightInKgs = weight;
    }
    public float getWeightInLbs() {
        float fWeightLbs = 0.45f * weightInKgs;
        return fWeightLbs;
    }
    static Settings getInstance() {
        if (instance != null)
            return instance;
        instance = new Settings();
        return instance;
    }
}
