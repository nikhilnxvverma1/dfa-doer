package com.madebynikhil.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.madebynikhil.editor.command.Command;
import com.madebynikhil.model.State;
import com.madebynikhil.model.StateMachine;
import com.madebynikhil.model.Transition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * Top level delegate class that manages most of the editor operations and holds the
 * root data model and corresponding views(generated from data model).
 * Created by NikhilVerma on 01/11/16.
 */
public class Workspace {

    private MainWindowController mainWindowController;

    private StateMachine stateMachine;
    private Stack<Command> history=new Stack<>();
    private Stack<Command> future=new Stack<>();
    private String filename;
    private File file;
    private DesignerController designerController;


    public Workspace(MainWindowController mainWindowController) {
        this(mainWindowController,null);
    }

    public Workspace(MainWindowController mainWindowController,String filename) {
        this.mainWindowController=mainWindowController;
        this.filename = filename;
        this.designerController=new DesignerController(this,mainWindowController.getDesigner());
        if(filename==null){
            this.stateMachine=new StateMachine();

        }else{
            //TODO load from json file
            //TODO initialize the views
        }
    }

    public MainWindowController getMainWindowController() {
        return mainWindowController;
    }

    public DesignerController getDesignerController() {
        return designerController;
    }

    public StateMachine getStateMachine() {
        return stateMachine;
    }

    /**
     * Sets the new symbol from a comma separated string
     * @param text comma seperated string received usualy from a textfield
     * @return null if everything is perfect, otherwise it spits out the invalid symbol
     */
    public String setNewSymbolsFrom(String text){

        String [] symbols=text.split(",");
        for (String symbol : symbols){
            if(symbol.length()!=1){
                return symbol;
            }
        }
        this.stateMachine.setSymbolList(Arrays.asList(symbols));
        return null;
    }

    public String getSymbolsAsCSV(){
        StringBuilder stringBuilder=new StringBuilder();

        Iterator<String> iterator = this.stateMachine.getSymbolList().iterator();
        while(iterator.hasNext()){
            String symbol =iterator.next();
            stringBuilder.append(symbol);
            if(iterator.hasNext()){
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    public void setNewDescription(String text){
        this.stateMachine.setDescription(text);
    }

    public void save(){
        this.saveAs(this.file);
    }

    public void saveAs(File file){
        Gson gson=new GsonBuilder().setPrettyPrinting().create();
        String json=gson.toJson(stateMachine);
        System.out.println("Json is :");
        System.out.println(json);
        try(  PrintWriter out = new PrintWriter( file )  ){
            out.println( json);
        } catch (FileNotFoundException e) {
            //TODO handle by showing GUI alert box
            e.printStackTrace();
        }
        this.file=file;
    }

    public void loadFromJsonFile(File file){
        Gson gson = new Gson();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
            stateMachine = gson.fromJson(reader, StateMachine.class);
            setTransientModelAttributesAndRemoveDuplicates();
            this.file=file;

        } catch (FileNotFoundException e) {
            //TODO alert the user
            e.printStackTrace();
        }
    }

    private void setTransientModelAttributesAndRemoveDuplicates(){
        //build a map of the states
        Map <String,State>stateMap=new HashMap<>();
        for(State state: stateMachine.getStateList()){
            stateMap.put(state.getName(),state);
        }

        //set transient to and from states in transitions
        for(State state: stateMachine.getStateList()){
            Set<Map.Entry<String, Transition>> entries = state.getOutgoingTransitionMap().entrySet();
            for(Map.Entry<String, Transition> entry : entries){
                String outgoingStateName = entry.getKey();
                Transition outgoingTransition=entry.getValue();
                outgoingTransition.setFrom(state);
                outgoingTransition.setTo(stateMap.get(outgoingStateName));
            }
        }

        //set the starting state to point to a single object instead of a duplicate
        State startingState=stateMap.get(stateMachine.getStartingState().getName());
        stateMachine.setStartingState(startingState);
    }

    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return file;
    }
    public boolean isEmptyDocument(){
        return stateMachine.getStateList().isEmpty();
    }

    public void initView() {
        //TODO
    }
}
