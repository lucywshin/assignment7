import controller.IGuiController;
import controller.IPortfolioController;
import controller.JFrameController;
import controller.PortfolioController;
import java.io.InputStreamReader;
import model.IPortfolioModel;
import model.PortfolioModel;
import view.IJFrameView;
import view.IPortfolioView;
import view.JFrameView;
import view.PortfolioView;

class Main {

  public static void main(String[] args) {
    IPortfolioModel model = new PortfolioModel();

    boolean isOldGui = false;

    for (String arg : args) {
      if (arg.equals("oldGui")) {
        isOldGui = true;
        break;
      }
    }

    if (isOldGui) {
      IPortfolioView view = new PortfolioView();
      IPortfolioController controller = new PortfolioController(model, view,
          new InputStreamReader(System.in), System.out);
      controller.runApplication();
    } else {
      IGuiController controller = new JFrameController(model);
      IJFrameView view = new JFrameView();
      controller.setView(view);
    }
  }
}
