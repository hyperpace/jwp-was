package utils.modelandview.view;

import utils.modelandview.model.Model;

import java.io.IOException;

public interface View {
    byte[] render(String location, Model model) throws IOException;
}
